package com.romanpulov.piastriawss.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "payment_objects")
public class PaymentObject extends OrderedEntitySuperclass implements CommonEntity {

    @Id
    @Column(name = "payment_object_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotEmpty
    @Column(name = "payment_object_name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "payment_object_period")
    private String period;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Column(name = "payment_object_term")
    private String term;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @Column(name = "payment_object_pay_delay")
    private Long payDelay;

    public Long getPayDelay() {
        return payDelay;
    }

    public void setPayDelay(Long payDelay) {
        this.payDelay = payDelay;
    }

    @OneToMany(mappedBy = "paymentObject", fetch = FetchType.LAZY)
    private final Set<Payment> payments = new HashSet<>();

    @PreRemove
    private void preRemove() {
        if (payments.size() > 0) {
            throw new RuntimeException("Unable to delete " + this + " because it has child payments");
        }
    }

    @Override
    public String toString() {
        return "PaymentObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", period='" + period + '\'' +
                ", term='" + term + '\'' +
                ", payDelay=" + payDelay +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentObject paymentObject = (PaymentObject) o;
        return id.equals(paymentObject.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
