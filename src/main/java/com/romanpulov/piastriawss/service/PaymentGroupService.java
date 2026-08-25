package com.romanpulov.piastriawss.service;

import com.romanpulov.piastriawss.entity.PaymentGroup;
import com.romanpulov.piastriawss.repository.CustomQueryRepository;
import com.romanpulov.piastriawss.repository.PaymentGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentGroupService extends AbstractOrderedEntityService<PaymentGroup, PaymentGroupRepository> {
    public PaymentGroupService(PaymentGroupRepository repository, CustomQueryRepository customQueryRepository) {
        super(repository, customQueryRepository);
    }

    @Override
    public Iterable<PaymentGroup> findAll() {
        return repository.findAllByOrderByOrderIdAsc();
    }

    public List<PaymentGroup> findByName(String name) {
        return repository.findByName(name);
    }
}
