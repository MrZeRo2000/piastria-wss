package com.romanpulov.piastriawss.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentImportDTO(Long paymentId, BigDecimal paymentAmount, LocalDate paymentDate) {
}
