package com.romanpulov.piastriawss.controller;

import com.romanpulov.piastriawss.dto.*;
import com.romanpulov.piastriawss.entity.Payment;
import com.romanpulov.piastriawss.entity.PaymentGroup;
import com.romanpulov.piastriawss.entity.PaymentObject;
import com.romanpulov.piastriawss.entitymapper.*;
import com.romanpulov.piastriawss.exception.CommonEntityNotFoundException;
import com.romanpulov.piastriawss.exception.NotFoundException;
import com.romanpulov.piastriawss.repository.PaymentGroupRepository;
import com.romanpulov.piastriawss.repository.PaymentObjectRepository;
import com.romanpulov.piastriawss.repository.PaymentRepository;
import com.romanpulov.piastriawss.repository.ProductRepository;
import com.romanpulov.piastriawss.service.PaymentObjectPaymentService;
import com.romanpulov.piastriawss.service.PaymentObjectService;
import com.romanpulov.piastriawss.service.PaymentService;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PaymentCustomController {

    private final PaymentRepository paymentRepository;

    private final PaymentObjectRepository paymentObjectRepository;

    private final PaymentGroupRepository paymentGroupRepository;

    private final ProductRepository productRepository;

    private final PaymentObjectDTOMapper paymentObjectDTOMapper;

    private final PaymentGroupDTOMapper paymentGroupDTOMapper;

    private final ProductDTOMapper productDTOMapper;

    private final PaymentRepDTOMapper paymentRepDTOMapper;

    private final PaymentObjectService paymentObjectService;

    private final PaymentService paymentService;

    private final PaymentObjectPaymentService paymentObjectPaymentService;

    protected final EntityDTOMapper<Payment, PaymentDTO> mapper;

    public PaymentCustomController(
            PaymentRepository repository,
            EntityDTOMapper<Payment, PaymentDTO> mapper,
            PaymentObjectRepository paymentObjectRepository,
            PaymentGroupRepository paymentGroupRepository,
            ProductRepository productRepository,
            PaymentObjectDTOMapper paymentObjectDTOMapper,
            PaymentGroupDTOMapper paymentGroupDTOMapper,
            ProductDTOMapper productDTOMapper,
            PaymentRepDTOMapper paymentRepDTOMapper,
            PaymentObjectService paymentObjectService,
            PaymentService paymentService,
            PaymentObjectPaymentService paymentObjectPaymentService
    ) {
        this.paymentRepository = repository;
        this.mapper = mapper;
        this.paymentObjectRepository = paymentObjectRepository;
        this.paymentGroupRepository = paymentGroupRepository;
        this.productRepository = productRepository;
        this.paymentObjectDTOMapper = paymentObjectDTOMapper;
        this.paymentGroupDTOMapper = paymentGroupDTOMapper;
        this.productDTOMapper = productDTOMapper;
        this.paymentRepDTOMapper = paymentRepDTOMapper;
        this.paymentObjectService = paymentObjectService;
        this.paymentService = paymentService;
        this.paymentObjectPaymentService = paymentObjectPaymentService;
    }

    @GetMapping("/payments:refs")
    ResponseEntity<PaymentRefsDTO> getRefsByPaymentObjectIdAndDate(
            @RequestParam("paymentObjectId")
                    Long paymentObjectId,
            @RequestParam("paymentPeriodDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDate paymentPeriodDate
    ) throws CommonEntityNotFoundException {

        List<PaymentObjectDTO> paymentObjectList = paymentObjectRepository
                .findAllByOrderByOrderIdAsc()
                .stream()
                .map(paymentObjectDTOMapper::entityToDTO)
                .toList();

        PaymentObject paymentObject = paymentObjectRepository
                .findById(paymentObjectId)
                .orElseThrow(() -> new CommonEntityNotFoundException(paymentObjectId)
        );

        PaymentObjectDTO paymentObjectDTO = paymentObjectDTOMapper.entityToDTO(paymentObject);

        List<PaymentGroupDTO> paymentGroupList = paymentGroupRepository
                .findAllByOrderByOrderIdAsc()
                .stream()
                .map(paymentGroupDTOMapper::entityToDTO)
                .collect(Collectors.toList());

        List<ProductDTO> productList = productRepository
                .findAllByOrderByOrderIdAsc()
                .stream()
                .map(productDTOMapper::entityToDTO)
                .collect(Collectors.toList());

        List<PaymentDTO> paymentList = paymentRepository
                .findByPaymentObjectIdAndPaymentPeriodDate(
                        paymentObject,
                        paymentPeriodDate,
                        Sort.by("paymentGroup.orderId", "product.orderId"))
                .stream()
                .map(mapper::entityToDTO)
                .collect(Collectors.toList());

        List<PaymentDTO> prevPeriodPaymentList = paymentRepository
                .findByPaymentObjectIdAndPaymentPeriodDate(
                        paymentObject,
                        paymentObjectService.getPaymentObjectPreviousPeriodPaymentDate(paymentObject, paymentPeriodDate),
                        Sort.by("paymentGroup.orderId", "product.orderId"))
                .stream()
                .map(mapper::entityToDTO)
                .collect(Collectors.toList());

        PaymentRefsDTO result = new PaymentRefsDTO(
                paymentList,
                prevPeriodPaymentList,
                paymentObjectDTO,
                paymentGroupList,
                productList
        );

        return ResponseEntity.ok(result);
    }

    @PostMapping("/payments:duplicate_previous_period")
    ResponseEntity<RowsAffectedDTO> duplicatePreviousPeriod(
            @RequestParam("paymentObjectId")
                    Long paymentObjectId,
            @RequestParam("paymentPeriodDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDate paymentPeriodDate
    ) {
        PaymentObject paymentObject = paymentObjectRepository.findById(paymentObjectId).orElseThrow(
                () -> new EntityNotFoundException("Payment object not found:" + paymentObjectId)
        );

        int rowsAffected = this.paymentService.duplicatePreviousPeriod(paymentObject, paymentPeriodDate);

        return ResponseEntity.ok(new RowsAffectedDTO(rowsAffected));
    }

    @GetMapping("/payments:payment_object_group_refs")
    ResponseEntity<PaymentObjectGroupRefsDTO> getPaymentObjectGroupRefs() {

        List<PaymentObjectDTO> paymentObjectList = paymentObjectRepository
                .findAllByOrderByOrderIdAsc()
                .stream()
                .map(paymentObjectDTOMapper::entityToDTO)
                .collect(Collectors.toList());

        List<PaymentGroupDTO> paymentGroupList = paymentGroupRepository
                .findAllByOrderByOrderIdAsc()
                .stream()
                .map(paymentGroupDTOMapper::entityToDTO)
                .collect(Collectors.toList());

        PaymentObjectGroupRefsDTO result = new PaymentObjectGroupRefsDTO(
                paymentObjectList,
                paymentGroupList
        );

        return ResponseEntity.ok(result);
    }

    @PostMapping(value="/payments:update_payment_group")
    ResponseEntity<RowsAffectedDTO> updatePaymentGroup (
            @RequestParam("paymentObjectId")
            Long paymentObjectId,
            @RequestParam("paymentGroupFromId")
            Long paymentGroupFromId,
            @RequestParam("paymentGroupToId")
            Long paymentGroupToId
    ) {

        PaymentObject paymentObject = new PaymentObject();
        paymentObject.setId(paymentObjectId);

        PaymentGroup paymentGroupFrom = new PaymentGroup();
        paymentGroupFrom.setId(paymentGroupFromId);

        PaymentGroup paymentGroupTo = new PaymentGroup();
        paymentGroupTo.setId(paymentGroupToId);

        int rowsAffected = paymentService.updatePaymentGroup(paymentObject, paymentGroupFrom, paymentGroupTo);
        return ResponseEntity.ok(new RowsAffectedDTO(rowsAffected));
    }

    @GetMapping("/payments:payment_object_totals_by_payment_period")
    ResponseEntity<List<PaymentObjectTotalsDTO>> getPaymentObjectTotalsByPaymentPeriod(
        @RequestParam("paymentPeriodDate")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDate paymentPeriodDate
    ) {
        List<PaymentObjectTotalsDTO> result = new ArrayList<>();

        Map<PaymentObject, BigDecimal> totals = paymentObjectPaymentService.getPaymentObjectTotalsByPaymentPeriod(paymentPeriodDate);
        totals.forEach((paymentObject, value) ->
            result.add(
                new PaymentObjectTotalsDTO(
                    paymentPeriodDate,
                    paymentObjectDTOMapper.entityToDTO(paymentObject),
                    value
                )
            )
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/payments:payment_object_period_by_id")
    ResponseEntity<PaymentObjectPeriodTotalDTO> getPaymentObjectPeriodById (
            @RequestParam("paymentObjectId")
                    Long paymentObjectId,
            @RequestParam("currentDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDate currentDate
    )  throws NotFoundException {
        return ResponseEntity.ok(paymentObjectPaymentService.getPaymentObjectPeriodById(paymentObjectId, currentDate));
    }

    @GetMapping("/payments:payment_object_totals_by_date")
    ResponseEntity<List<PaymentObjectPeriodTotalDTO>> getPaymentObjectPeriodTotal(
            @RequestParam("currentDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDate currentDate
    ) {
        return ResponseEntity.ok(paymentObjectPaymentService.getPaymentObjectPeriodTotal(currentDate));
    }

    @GetMapping("/payments:payments_by_payment_object_and_payment_period_date_range")
    ResponseEntity<PaymentRepRefsDTO> getPaymentsByPaymentObjectAndPaymentDateRange(
            @RequestParam("paymentObjectId")
                    Long paymentObjectId,
            @RequestParam("paymentPeriodDateStart")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDate paymentPeriodDateStart,
            @RequestParam("paymentPeriodDateEnd")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDate paymentPeriodDateEnd
    ) {
        PaymentObject paymentObject = paymentObjectRepository.findById(paymentObjectId).orElseThrow(
                () -> new EntityNotFoundException("Payment object not found:" + paymentObjectId)
        );

        List<PaymentRepDTO> rows =
            paymentRepository.findByPaymentObjectAndPaymentPeriodDateBetween(
                    paymentObject,
                    paymentPeriodDateStart,
                    paymentPeriodDateEnd,
                    Sort.by("paymentPeriodDate").ascending()
            )
            .stream()
            .map(paymentRepDTOMapper::entityToDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(new PaymentRepRefsDTO(paymentObjectDTOMapper.entityToDTO(paymentObject), rows));
    }
}
