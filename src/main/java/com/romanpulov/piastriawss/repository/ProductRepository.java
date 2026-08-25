package com.romanpulov.piastriawss.repository;

import com.romanpulov.piastriawss.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findAllByOrderByOrderIdAsc();
}

