package com.romanpulov.piastriawss.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import java.util.*;

@Entity
@Table(name = "products")
public class Product extends OrderedEntitySuperclass implements CommonEntity {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotEmpty
    @Column(name = "product_name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "product_unit_name")
    private String unitName;

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    @Column(name = "product_counter_precision")
    private Long counterPrecision;

    public Long getCounterPrecision() {
        return counterPrecision;
    }

    public void setCounterPrecision(Long counterPrecision) {
        this.counterPrecision = counterPrecision;
    }

    @OneToMany(mappedBy = "product")
    private final Set<Payment> payments = new HashSet<>();

    @PreRemove
    private void preRemove() {
        if (payments.size() > 0) {
            throw new RuntimeException("Unable to delete " + this + " because it has child payments");
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unitName='" + unitName + '\'' +
                ", counterPrecision='" + counterPrecision + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
