package com.romanpulov.piastriawss.controller;

import com.romanpulov.piastriawss.dto.IdAmountDTO;
import com.romanpulov.piastriawss.dto.PatchRequestDTO;
import com.romanpulov.piastriawss.dto.PaymentDTO;
import com.romanpulov.piastriawss.entity.Payment;
import com.romanpulov.piastriawss.entitymapper.EntityDTOMapper;
import com.romanpulov.piastriawss.exception.CommonEntityNotFoundException;
import com.romanpulov.piastriawss.service.PaymentService;
import com.romanpulov.piastriawss.vo.PaymentAmountType;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/payments", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentController extends AbstractOrderedServiceRestController<Payment, PaymentDTO, PaymentService> {

    public PaymentController(
            PaymentService paymentService,
            EntityDTOMapper<Payment, PaymentDTO> mapper
            ) {
        super(paymentService, mapper, LoggerFactory.getLogger(PaymentController.class));
    }

    @PatchMapping("/{id}")
    ResponseEntity<IdAmountDTO> partialUpdate (
            @PathVariable Long id,
            @RequestBody PatchRequestDTO patchRequest
    ) throws CommonEntityNotFoundException, BadPatchRequestException {
        if (!patchRequest.getOp().equals("replace")) {
            throw new BadPatchRequestException("operation", patchRequest.getOp());
        }

        BigDecimal updateValue = null;
        if (patchRequest.getValue() != null) {
            try {
                updateValue = new BigDecimal(patchRequest.getValue());
            } catch (RuntimeException e) {
                throw new BadPatchRequestException("value", patchRequest.getValue());
            }
        }

        PaymentAmountType paymentAmountType = PaymentAmountType.fromString(patchRequest.getPath());
        if (paymentAmountType == PaymentAmountType.AT_UNKNOWN) {
            throw new BadPatchRequestException("path", patchRequest.getPath());
        }

        return ResponseEntity.ok(entityService.updatePaymentAmount(id, paymentAmountType, updateValue));
    }
}
