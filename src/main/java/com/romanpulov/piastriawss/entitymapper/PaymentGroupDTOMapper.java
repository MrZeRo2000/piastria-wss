package com.romanpulov.piastriawss.entitymapper;

import com.romanpulov.piastriawss.dto.PaymentGroupDTO;
import com.romanpulov.piastriawss.entity.PaymentGroup;
import org.springframework.stereotype.Component;

@Component
public class PaymentGroupDTOMapper implements EntityDTOMapper<PaymentGroup, PaymentGroupDTO> {
    @Override
    public PaymentGroupDTO entityToDTO(PaymentGroup entity) {
        return new PaymentGroupDTO(entity.getId(), entity.getName(), entity.getUrl(), entity.getColor());
    }

    public PaymentGroupDTO entityIdNameToDTO(PaymentGroup entity) {
        return new PaymentGroupDTO(entity.getId(), entity.getName(), null, null);
    }

    public PaymentGroupDTO entityIdNameColorToDTO(PaymentGroup entity) {
        return new PaymentGroupDTO(entity.getId(), entity.getName(), null, entity.getColor());
    }

    @Override
    public PaymentGroup dtoTOEntity(PaymentGroupDTO dto) {
        PaymentGroup entity = new PaymentGroup();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setUrl(dto.getUrl());
        entity.setColor(dto.getColor());
        return entity;
    }

    @Override
    public Class<?> getEntityClass() {
        return PaymentGroup.class;
    }
}
