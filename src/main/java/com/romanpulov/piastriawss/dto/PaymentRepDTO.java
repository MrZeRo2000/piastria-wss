package com.romanpulov.piastriawss.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.romanpulov.piastriawss.dto.serializer.LocalDateDeserializer;
import com.romanpulov.piastriawss.dto.serializer.LocalDateSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRepDTO {
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate periodDate;

    private PaymentGroupDTO paymentGroup;

    private ProductDTO product;

    private BigDecimal paymentAmount;

    private BigDecimal commissionAmount;

    @JsonCreator
    public PaymentRepDTO(
            @JsonProperty("periodDate") LocalDate periodDate,
            @JsonProperty("paymentGroup") PaymentGroupDTO paymentGroup,
            @JsonProperty("product") ProductDTO product,
            @JsonProperty("paymentAmount") BigDecimal paymentAmount,
            @JsonProperty("commissionAmount") BigDecimal commissionAmount
    ) {
        this.periodDate = periodDate;
        this.paymentGroup = paymentGroup;
        this.product = product;
        this.paymentAmount = paymentAmount;
        this.commissionAmount = commissionAmount;
    }

    public LocalDate getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(LocalDate periodDate) {
        this.periodDate = periodDate;
    }

    public PaymentGroupDTO getPaymentGroup() {
        return paymentGroup;
    }

    public void setPaymentGroup(PaymentGroupDTO paymentGroup) {
        this.paymentGroup = paymentGroup;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentRepDTO that = (PaymentRepDTO) o;
        return periodDate.equals(that.periodDate) &&
                paymentGroup.equals(that.paymentGroup) &&
                product.equals(that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodDate, paymentGroup, product);
    }

    @Override
    public String toString() {
        return "PaymentRepDTO{" +
                "periodDate=" + periodDate +
                ", paymentGroup=" + paymentGroup +
                ", product=" + product +
                ", paymentAmount=" + paymentAmount +
                ", commissionAmount=" + commissionAmount +
                '}';
    }
}
