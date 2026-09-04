package com.romanpulov.piastriawss.dto;

import java.math.BigDecimal;

public record PaymentImportDTO(Long paymentId, BigDecimal paymentAmount) {
}
