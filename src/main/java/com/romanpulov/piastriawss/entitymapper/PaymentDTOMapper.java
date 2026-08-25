package com.romanpulov.piastriawss.entitymapper;

import com.romanpulov.piastriawss.dto.PaymentDTO;
import com.romanpulov.piastriawss.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentDTOMapper implements EntityDTOMapper<Payment, PaymentDTO> {

    private final PaymentObjectDTOMapper paymentObjectDTOMapper;
    private final PaymentGroupDTOMapper paymentGroupDTOMapper;
    private final ProductDTOMapper productDTOMapper;

    public PaymentDTOMapper(
            PaymentObjectDTOMapper paymentObjectDTOMapper,
            PaymentGroupDTOMapper paymentGroupDTOMapper,
            ProductDTOMapper productDTOMapper
    ) {
        this.paymentObjectDTOMapper = paymentObjectDTOMapper;
        this.paymentGroupDTOMapper = paymentGroupDTOMapper;
        this.productDTOMapper = productDTOMapper;
    }

    @Override
    public PaymentDTO entityToDTO(Payment entity) {
        return new PaymentDTO(
            entity.getId(),
            entity.getPaymentDate(),
            entity.getPaymentPeriodDate(),
            paymentObjectDTOMapper.entityToDTO(entity.getPaymentObject()),
            paymentGroupDTOMapper.entityToDTO(entity.getPaymentGroup()),
            productDTOMapper.entityToDTO(entity.getProduct()),
            entity.getProductCounter(),
            entity.getPaymentAmount(),
            entity.getCommissionAmount()
        );
    }

    @Override
    public Payment dtoTOEntity(PaymentDTO dto) {
        Payment entity = new Payment();

        entity.setId(dto.getId());

        entity.setPaymentDate(dto.getDate());
        entity.setPaymentPeriodDate(dto.getPeriodDate());
        entity.setPaymentObject(paymentObjectDTOMapper.dtoTOEntity(dto.getPaymentObject()));
        entity.setPaymentGroup(paymentGroupDTOMapper.dtoTOEntity(dto.getPaymentGroup()));
        entity.setProduct(productDTOMapper.dtoTOEntity(dto.getProduct()));
        entity.setProductCounter(dto.getProductCounter());
        entity.setPaymentAmount(dto.getPaymentAmount());
        entity.setCommissionAmount(dto.getCommissionAmount());

        return entity;
    }

    @Override
    public Class<?> getEntityClass() {
        return Payment.class;
    }
}
