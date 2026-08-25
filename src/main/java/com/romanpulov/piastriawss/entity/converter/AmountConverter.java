package com.romanpulov.piastriawss.entity.converter;

import jakarta.persistence.AttributeConverter;
import java.math.BigDecimal;
import java.math.MathContext;

public class AmountConverter implements AttributeConverter<BigDecimal, Long> {
    public static final BigDecimal SCALE =  BigDecimal.valueOf(100L);

    @Override
    public Long convertToDatabaseColumn(BigDecimal attribute) {
        return attribute == null? null : attribute.multiply(SCALE).longValue();
    }

    @Override
    public BigDecimal convertToEntityAttribute(Long dbData) {
        return dbData == null? null : BigDecimal.valueOf(dbData).divide(SCALE, MathContext.DECIMAL32);
    }
}
