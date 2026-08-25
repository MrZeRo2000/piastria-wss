package com.romanpulov.piastriawss.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "payment_groups")
public class PaymentGroup extends OrderedEntitySuperclass implements CommonEntity {

    @Id
    @Column(name = "payment_group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotEmpty
    @Column(name = "payment_group_name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "payment_group_url")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "payment_group_color")
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @OneToMany(mappedBy = "paymentGroup")
    private final Set<Payment> payments = new HashSet<>();

    @PreRemove
    private void preRemove() {
        if (payments.size() > 0) {
            throw new RuntimeException("Unable to delete " + this + " because it has child payments");
        }
    }

    @Override
    public String toString() {
        return "PaymentGroup{" +
                "id=" + id +
                ", order_id=" + getOrderId() +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentGroup that = (PaymentGroup) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
