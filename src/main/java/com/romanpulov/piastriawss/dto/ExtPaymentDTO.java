package com.romanpulov.piastriawss.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class ExtPaymentDTO {
    private final String productName;

    private final String groupName;

    private final LocalDate paymentPeriodDate;

    private final BigDecimal productCounter;

    private final BigDecimal paymentAmount;

    private final BigDecimal commissionAmount;

    public ExtPaymentDTO(String productName, String groupName, LocalDate paymentPeriodDate,
                         BigDecimal productCounter, BigDecimal paymentAmount, BigDecimal commissionAmount) {
        this.productName = productName;
        this.groupName = groupName;
        this.paymentPeriodDate = paymentPeriodDate;
        this.productCounter = productCounter;
        this.paymentAmount = paymentAmount;
        this.commissionAmount = commissionAmount;
    }

    public String getProductName() {
        return productName;
    }

    public String getGroupName() {
        return groupName;
    }

    public LocalDate getPaymentPeriodDate() {
        return paymentPeriodDate;
    }

    public BigDecimal getProductCounter() {
        return productCounter;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtPaymentDTO that = (ExtPaymentDTO) o;
        return productName.equals(that.productName) &&
                groupName.equals(that.groupName) &&
                paymentPeriodDate.equals(that.paymentPeriodDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, groupName, paymentPeriodDate);
    }

    @Override
    public String toString() {
        return "ExtPaymentDTO{" +
                "productName='" + productName + '\'' +
                ", groupName='" + groupName + '\'' +
                ", PaymentPeriodDate=" + paymentPeriodDate +
                ", productCounter=" + productCounter +
                ", paymentAmount=" + paymentAmount +
                ", commissionAmount=" + commissionAmount +
                '}';
    }
}
