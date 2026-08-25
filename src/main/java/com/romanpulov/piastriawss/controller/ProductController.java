package com.romanpulov.piastriawss.controller;

import com.romanpulov.piastriawss.dto.ProductDTO;
import com.romanpulov.piastriawss.entity.Product;
import com.romanpulov.piastriawss.entitymapper.EntityDTOMapper;

import com.romanpulov.piastriawss.service.ProductService;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController extends AbstractOrderedServiceRestController<Product, ProductDTO, ProductService> {
    public ProductController(ProductService productService, EntityDTOMapper<Product, ProductDTO> mapper) {
        super(productService, mapper, LoggerFactory.getLogger(ProductController.class));
    }
}
