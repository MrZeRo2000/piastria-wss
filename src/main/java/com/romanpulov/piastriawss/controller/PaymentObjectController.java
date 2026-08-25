package com.romanpulov.piastriawss.controller;

import com.romanpulov.piastriawss.dto.PaymentObjectDTO;
import com.romanpulov.piastriawss.entity.PaymentObject;
import com.romanpulov.piastriawss.entitymapper.EntityDTOMapper;
import com.romanpulov.piastriawss.service.PaymentObjectService;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/payment-objects", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentObjectController extends AbstractOrderedServiceRestController<PaymentObject, PaymentObjectDTO, PaymentObjectService> {

    public PaymentObjectController(
            PaymentObjectService paymentObjectService,
            EntityDTOMapper<PaymentObject, PaymentObjectDTO> paymentObjectDTOMapper
    ) {
        super(paymentObjectService, paymentObjectDTOMapper, LoggerFactory.getLogger(PaymentObjectController.class));
    }

}
