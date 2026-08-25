package com.romanpulov.piastriawss.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.romanpulov.piastriawss.dto.serializer.LocalDateDeserializer;
import com.romanpulov.piastriawss.dto.serializer.LocalDateSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentObjectTotalsDTO {

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private final LocalDate periodDate;

    private final PaymentObjectDTO paymentObject;

    private final BigDecimal totalAmount;

    public LocalDate getPeriodDate() {
        return periodDate;
    }

    public PaymentObjectDTO getPaymentObject() {
        return paymentObject;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @JsonCreator
    public PaymentObjectTotalsDTO(
            LocalDate periodDate,
            PaymentObjectDTO paymentObject,
            BigDecimal totalAmount) {
        this.periodDate = periodDate;
        this.paymentObject = paymentObject;
        this.totalAmount = totalAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentObjectTotalsDTO that = (PaymentObjectTotalsDTO) o;
        return periodDate.equals(that.periodDate) &&
                paymentObject.equals(that.paymentObject) &&
                Objects.equals(totalAmount, that.totalAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodDate, paymentObject, totalAmount);
    }

    @Override
    public String toString() {
        return "PaymentObjectTotalsDTO{" +
                "periodDate=" + periodDate +
                ", paymentObject=" + paymentObject +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
