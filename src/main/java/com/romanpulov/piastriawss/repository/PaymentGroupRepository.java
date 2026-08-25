package com.romanpulov.piastriawss.repository;

import com.romanpulov.piastriawss.entity.PaymentGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentGroupRepository extends CrudRepository<PaymentGroup, Long> {
    List<PaymentGroup> findAllByOrderByOrderIdAsc();
    List<PaymentGroup> findByName(String name);
}
