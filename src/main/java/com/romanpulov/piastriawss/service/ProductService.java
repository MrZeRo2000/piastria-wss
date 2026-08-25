package com.romanpulov.piastriawss.service;

import com.romanpulov.piastriawss.entity.Product;
import com.romanpulov.piastriawss.repository.CustomQueryRepository;
import com.romanpulov.piastriawss.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends AbstractOrderedEntityService<Product, ProductRepository> {
    public ProductService(ProductRepository repository, CustomQueryRepository customQueryRepository) {
        super(repository, customQueryRepository);
    }

    @Override
    public Iterable<Product> findAll() {
        return repository.findAllByOrderByOrderIdAsc();
    }
}
