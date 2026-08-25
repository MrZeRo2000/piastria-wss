package com.romanpulov.piastriawss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.piastriawss.dto.serializer.LocalDateSerializer;
import tools.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentObjectPeriodTotalDTO {

    private final PaymentObjectDTO paymentObject;

    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate paymentDate;

    private final BigDecimal paymentAmount;

    private final Boolean paymentOverdue;

    public PaymentObjectDTO getPaymentObject() {
        return paymentObject;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public Boolean getPaymentOverdue() {
        return paymentOverdue;
    }

    public PaymentObjectPeriodTotalDTO(PaymentObjectDTO paymentObject, LocalDate paymentDate, BigDecimal paymentAmount, Boolean paymentOverdue) {
        this.paymentObject = paymentObject;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.paymentOverdue = paymentOverdue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentObjectPeriodTotalDTO that = (PaymentObjectPeriodTotalDTO) o;
        return paymentObject.equals(that.paymentObject) &&
                Objects.equals(paymentDate, that.paymentDate) &&
                Objects.equals(paymentAmount, that.paymentAmount) &&
                Objects.equals(paymentOverdue, that.paymentOverdue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentObject, paymentDate, paymentAmount, paymentOverdue);
    }

    @Override
    public String toString() {
        return "PaymentObjectPeriodTotalDTO{" +
                "paymentObject=" + paymentObject +
                ", paymentDate=" + paymentDate +
                ", paymentAmount=" + paymentAmount +
                ", paymentOverdue=" + paymentOverdue +
                '}';
    }
}
