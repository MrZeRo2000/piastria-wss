package com.romanpulov.piastriawss.repository;

import com.romanpulov.piastriawss.entity.TestEntity;
import org.springframework.data.repository.CrudRepository;

public interface TestRepository extends CrudRepository<TestEntity, Long> {
}
