package com.romanpulov.piastriawss.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class OrderedEntitySuperclass implements OrderedEntity {

    @Column(name = "order_id")
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
