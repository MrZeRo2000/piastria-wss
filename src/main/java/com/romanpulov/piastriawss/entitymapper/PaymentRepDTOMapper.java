package com.romanpulov.piastriawss.entitymapper;

import com.romanpulov.piastriawss.dto.PaymentRepDTO;
import com.romanpulov.piastriawss.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentRepDTOMapper implements EntityDTOMapper<Payment, PaymentRepDTO> {

    private final PaymentGroupDTOMapper paymentGroupDTOMapper;
    private final ProductDTOMapper productDTOMapper;

    public PaymentRepDTOMapper(
            PaymentGroupDTOMapper paymentGroupDTOMapper,
            ProductDTOMapper productDTOMapper
    ) {
        this.paymentGroupDTOMapper = paymentGroupDTOMapper;
        this.productDTOMapper = productDTOMapper;
    }

    @Override
    public PaymentRepDTO entityToDTO(Payment entity) {
        return new PaymentRepDTO(
                entity.getPaymentPeriodDate(),
                paymentGroupDTOMapper.entityIdNameColorToDTO(entity.getPaymentGroup()),
                productDTOMapper.entityIdNameToDTO(entity.getProduct()),
                entity.getPaymentAmount(),
                entity.getCommissionAmount()
        );
    }

    @Override
    public Payment dtoTOEntity(PaymentRepDTO dto) {
        Payment entity = new Payment();
        entity.setPaymentPeriodDate(dto.getPeriodDate());
        entity.setPaymentGroup(paymentGroupDTOMapper.dtoTOEntity(dto.getPaymentGroup()));
        entity.setProduct(productDTOMapper.dtoTOEntity(dto.getProduct()));
        entity.setPaymentAmount(dto.getPaymentAmount());
        entity.setCommissionAmount(dto.getCommissionAmount());

        return entity;
    }

    @Override
    public Class<?> getEntityClass() {
        return Payment.class;
    }
}
