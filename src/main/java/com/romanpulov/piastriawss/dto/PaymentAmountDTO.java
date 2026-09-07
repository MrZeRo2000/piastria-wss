package com.romanpulov.piastriawss.dto;

import java.math.BigDecimal;

public record PaymentAmountDTO(Long paymentId, BigDecimal paymentAmount) {
}
