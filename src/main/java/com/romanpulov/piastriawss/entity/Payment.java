package com.romanpulov.piastriawss.entity;

import com.romanpulov.piastriawss.entity.converter.AmountConverter;
import com.romanpulov.piastriawss.entity.converter.DateConverter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "payments")
public class Payment implements CommonEntity {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "payment_date")
    @Convert(converter = DateConverter.class)
    private LocalDate paymentDate;

    @NotNull
    @Column(name = "payment_period_date")
    @Convert(converter = DateConverter.class)
    private LocalDate paymentPeriodDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "payment_object_id", referencedColumnName = "payment_object_id")
    private PaymentObject paymentObject;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "payment_group_id", referencedColumnName = "payment_group_id")
    private PaymentGroup paymentGroup;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @Column(name = "product_counter")
    @PositiveOrZero
    @Convert(converter = AmountConverter.class)
    private BigDecimal productCounter;

    @NotNull
    @PositiveOrZero
    @Convert(converter = AmountConverter.class)
    @Column(name = "payment_amount")
    private BigDecimal paymentAmount;

    @NotNull
    @PositiveOrZero
    @Convert(converter = AmountConverter.class)
    @Column(name = "commission_amount")
    private BigDecimal commissionAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDate getPaymentPeriodDate() {
        return paymentPeriodDate;
    }

    public void setPaymentPeriodDate(LocalDate paymentPeriodDate) {
        this.paymentPeriodDate = paymentPeriodDate;
    }

    public PaymentObject getPaymentObject() {
        return paymentObject;
    }

    public void setPaymentObject(PaymentObject paymentObject) {
        this.paymentObject = paymentObject;
    }

    public PaymentGroup getPaymentGroup() {
        return paymentGroup;
    }

    public void setPaymentGroup(PaymentGroup paymentGroup) {
        this.paymentGroup = paymentGroup;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
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
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", paymentDate=" + paymentDate +
                ", paymentPeriodDate=" + paymentPeriodDate +
                ", paymentObject=" + paymentObject +
                ", paymentGroup=" + paymentGroup +
                ", product=" + product +
                ", productCounter='" + productCounter + '\'' +
                ", paymentAmount=" + paymentAmount +
                ", commissionAmount=" + commissionAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return id.equals(payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
