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
public class PaymentDTO {
    private Long id;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate date;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate periodDate;

    private PaymentObjectDTO paymentObject;

    private PaymentGroupDTO paymentGroup;

    private ProductDTO product;

    private BigDecimal productCounter;

    private BigDecimal paymentAmount;

    private BigDecimal commissionAmount;

    @JsonCreator
    public PaymentDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("date") LocalDate date,
            @JsonProperty("periodDate") LocalDate periodDate,
            @JsonProperty("paymentObject") PaymentObjectDTO paymentObject,
            @JsonProperty("paymentGroup") PaymentGroupDTO paymentGroup,
            @JsonProperty("product") ProductDTO product,
            @JsonProperty("productCounter") BigDecimal productCounter,
            @JsonProperty("paymentAmount") BigDecimal paymentAmount,
            @JsonProperty("commissionAmount") BigDecimal commissionAmount
    ) {
        this.id = id;
        this.date = date;
        this.periodDate = periodDate;
        this.paymentObject = paymentObject;
        this.paymentGroup = paymentGroup;
        this.product = product;
        this.productCounter = productCounter;
        this.paymentAmount = paymentAmount;
        this.commissionAmount = commissionAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(LocalDate periodDate) {
        this.periodDate = periodDate;
    }

    public PaymentObjectDTO getPaymentObject() {
        return paymentObject;
    }

    public void setPaymentObject(PaymentObjectDTO paymentObject) {
        this.paymentObject = paymentObject;
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

    public BigDecimal getProductCounter() {
        return productCounter;
    }

    public void setProductCounter(BigDecimal productCounter) {
        this.productCounter = productCounter;
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
        PaymentDTO that = (PaymentDTO) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "id=" + id +
                ", date=" + date +
                ", periodDate=" + periodDate +
                ", paymentObject=" + paymentObject +
                ", paymentGroup=" + paymentGroup +
                ", product=" + product +
                ", productCounter=" + productCounter +
                ", paymentAmount=" + paymentAmount +
                ", commissionAmount=" + commissionAmount +
                '}';
    }
}
