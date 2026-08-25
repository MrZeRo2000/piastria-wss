package com.romanpulov.piastriawss.service;

import com.romanpulov.piastriawss.entity.Payment;
import com.romanpulov.piastriawss.entity.PaymentObject;
import com.romanpulov.piastriawss.repository.CustomQueryRepository;
import com.romanpulov.piastriawss.repository.PaymentObjectRepository;
import com.romanpulov.piastriawss.vo.Period;
import com.romanpulov.piastriawss.vo.PeriodType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PaymentObjectService extends AbstractOrderedEntityService<PaymentObject, PaymentObjectRepository> {
    public PaymentObjectService(PaymentObjectRepository repository, CustomQueryRepository customQueryRepository) {
        super(repository, customQueryRepository);
    }

    @Override
    public Iterable<PaymentObject> findAll() {
        return repository.findAllByOrderByOrderIdAsc();
    }

    public Optional<PaymentObject> findById(Long id) {
        return repository.findById(id);
    }

    public Long getPaymentObjectPayDelay(PaymentObject paymentObject) {
        return paymentObject.getPayDelay() == null ? 1 : paymentObject.getPayDelay();
    }

    public PeriodType getPaymentObjectPeriodType(PaymentObject paymentObject) {
        PeriodType paymentPeriodType;
        try {
            paymentPeriodType = PeriodType.valueOf(paymentObject.getPeriod());
        } catch (IllegalArgumentException | NullPointerException e) {
            paymentPeriodType = PeriodType.defaultValue();
        }

        return paymentPeriodType;

    }

    public LocalDate getPaymentObjectPaymentDate(PaymentObject paymentObject, LocalDate currentDate) {
        PeriodType paymentPeriodType = getPaymentObjectPeriodType(paymentObject);

        Long payDelay = getPaymentObjectPayDelay(paymentObject);

        Period paymentPeriod = new Period(paymentPeriodType, -payDelay);

        LocalDate currentDateTruncated = Period.truncateToPeriodType(currentDate, paymentPeriodType);

        return paymentPeriod.addToDate(currentDateTruncated);
    }

    public LocalDate getPaymentObjectPreviousPeriodPaymentDate(PaymentObject paymentObject, LocalDate paymentPeriodDate) {
        PeriodType paymentPeriodType = getPaymentObjectPeriodType(paymentObject);
        Period paymentPeriod = new Period(paymentPeriodType, -1);

        paymentPeriodDate = Period.truncateToPeriodType(paymentPeriodDate, paymentPeriodType);

        return paymentPeriod.addToDate(paymentPeriodDate);
    }

    public LocalDate getPaymentObjectDueDate(PaymentObject paymentObject, LocalDate currentDate) {
        LocalDate dueDate = null;

        String paymentTerm = paymentObject.getTerm();

        if (paymentTerm != null && !paymentTerm.isEmpty()) {
            try {
                PeriodType paymentPeriodType = PeriodType.valueOf(paymentObject.getPeriod());

                LocalDate currentDateTruncated = Period.truncateToPeriodType(currentDate, paymentPeriodType);

                Long payDelay = getPaymentObjectPayDelay(paymentObject);

                Period paymentPeriod = new Period(paymentPeriodType, -payDelay);

                Period paymentTermPeriod = Period.fromString(paymentTerm);
                if (paymentTermPeriod != null) {
                    LocalDate startDueDate = payDelay == 0 ? paymentPeriod.addToDate(currentDateTruncated) : currentDateTruncated;
                    dueDate = paymentTermPeriod.addToDate(startDueDate);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        return dueDate;
    }

}
