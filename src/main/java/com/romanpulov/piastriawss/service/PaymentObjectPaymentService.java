package com.romanpulov.piastriawss.service;

import com.romanpulov.piastriawss.dto.PaymentObjectPeriodTotalDTO;
import com.romanpulov.piastriawss.entity.Payment;
import com.romanpulov.piastriawss.entity.PaymentObject;
import com.romanpulov.piastriawss.entity.converter.AmountConverter;
import com.romanpulov.piastriawss.entitymapper.PaymentObjectDTOMapper;
import com.romanpulov.piastriawss.exception.NotFoundException;
import com.romanpulov.piastriawss.repository.PaymentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentObjectPaymentService {
    private final PaymentObjectService paymentObjectService;

    private final PaymentRepository paymentRepository;

    private final PaymentObjectDTOMapper paymentObjectDTOMapper;

    public PaymentObjectPaymentService(PaymentObjectService paymentObjectService, PaymentRepository paymentRepository, PaymentObjectDTOMapper paymentObjectDTOMapper) {
        this.paymentObjectService = paymentObjectService;
        this.paymentRepository = paymentRepository;
        this.paymentObjectDTOMapper = paymentObjectDTOMapper;
    }

    public Map<PaymentObject, BigDecimal> getPaymentObjectTotalsByPaymentPeriod(LocalDate paymentPeriodDate) {

        List<Payment> paymentList = paymentRepository.findAllByPaymentPeriodDate(paymentPeriodDate);

        Map<PaymentObject, BigDecimal> paymentListTotals =
                paymentList
                        .stream()
                        .collect(Collectors.groupingBy(
                                Payment::getPaymentObject,
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Payment::getPaymentAmount,
                                        BigDecimal::add
                                )
                                )
                        );

        Map<PaymentObject, BigDecimal> result = new LinkedHashMap<>();

        paymentObjectService.findAll().forEach(paymentObject -> {
            BigDecimal value = paymentListTotals.get(paymentObject);
            result.put(paymentObject, Optional.ofNullable(value).orElse(BigDecimal.ZERO));
        });

        return result;
    }

    public BigDecimal getTotalByPaymentObjectAndPaymentPeriod(PaymentObject paymentObject, LocalDate paymentPeriodDate) {
        List<Payment> paymentList = paymentRepository.findByPaymentObjectIdAndPaymentPeriodDate(
                paymentObject,
                paymentPeriodDate,
                Sort.unsorted()
        );

        return paymentList.stream().map(Payment::getPaymentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalAmountByPaymentObjectAndPaymentPeriod(PaymentObject paymentObject, LocalDate paymentPeriodDate) {
        BigDecimal repositoryResult = paymentRepository.sumByPaymentObjectIdAndPaymentPeriodDate(paymentObject, paymentPeriodDate);
        return repositoryResult == null ? BigDecimal.ZERO : repositoryResult.divide(AmountConverter.SCALE, MathContext.DECIMAL32);
    }

    public PaymentObjectPeriodTotalDTO getPaymentObjectPeriodById(Long id, LocalDate currentDate) throws NotFoundException {
        PaymentObject paymentObject = paymentObjectService.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Payment object not found by id:{%d}", id))
        );
        LocalDate paymentDate = paymentObjectService.getPaymentObjectPaymentDate(paymentObject, currentDate);

        return new PaymentObjectPeriodTotalDTO(
                paymentObjectDTOMapper.entityToDTO(paymentObject),
                paymentDate,
                null,
                null
        );
    }

    public List<PaymentObjectPeriodTotalDTO> getPaymentObjectPeriodTotal(LocalDate currentDate) {
        List<PaymentObjectPeriodTotalDTO> result = new ArrayList<>();

        paymentObjectService.findAll().forEach(paymentObject -> {

            // calc paymentDate and overdue
            LocalDate paymentDate = null;
            LocalDate dueDate = null;

            if (paymentObject.getPeriod() != null) {
                paymentDate = paymentObjectService.getPaymentObjectPaymentDate(paymentObject, currentDate);
                dueDate = paymentObjectService.getPaymentObjectDueDate(paymentObject, currentDate);
            }

            // calc totalAmount
            BigDecimal totalAmount = (paymentDate == null) ? BigDecimal.ZERO :
                    getTotalAmountByPaymentObjectAndPaymentPeriod(paymentObject, paymentDate);

            // calc previous period totalAmount
            BigDecimal previousPeriodTotalAmount;
            if (totalAmount.equals(BigDecimal.ZERO) && (paymentDate != null)) {
                LocalDate previousPeriodPaymentDate = paymentObjectService.getPaymentObjectPreviousPeriodPaymentDate(
                        paymentObject,
                        paymentDate);
                previousPeriodTotalAmount = getTotalAmountByPaymentObjectAndPaymentPeriod(paymentObject, previousPeriodPaymentDate);
            } else {
                previousPeriodTotalAmount = BigDecimal.ZERO;
            }

            boolean paymentOverdue = (dueDate != null)
                    && totalAmount.equals(BigDecimal.ZERO)
                    && (currentDate.isAfter(dueDate) || previousPeriodTotalAmount.equals(BigDecimal.ZERO)
            );

            result.add(new PaymentObjectPeriodTotalDTO(
                    paymentObjectDTOMapper.entityToDTO(paymentObject),
                    paymentDate,
                    totalAmount,
                    paymentOverdue
            ));

        });


        return result;
    }
}
