package com.romanpulov.piastriawss.controller;

import com.romanpulov.piastriawss.dto.PaymentGroupDTO;
import com.romanpulov.piastriawss.entity.PaymentGroup;
import com.romanpulov.piastriawss.entitymapper.EntityDTOMapper;
import com.romanpulov.piastriawss.service.PaymentGroupService;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/payment-groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentGroupController extends AbstractOrderedServiceRestController<PaymentGroup, PaymentGroupDTO, PaymentGroupService> {

    public PaymentGroupController(
            PaymentGroupService paymentGroupService,
            EntityDTOMapper<PaymentGroup, PaymentGroupDTO> mapper)
    {
        super(paymentGroupService, mapper, LoggerFactory.getLogger(PaymentGroupController.class));
    }
}
