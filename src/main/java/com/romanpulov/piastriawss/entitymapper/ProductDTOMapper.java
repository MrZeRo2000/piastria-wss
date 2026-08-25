package com.romanpulov.piastriawss.entitymapper;

import com.romanpulov.piastriawss.dto.ProductDTO;
import com.romanpulov.piastriawss.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductDTOMapper implements EntityDTOMapper<Product, ProductDTO> {
    @Override
    public ProductDTO entityToDTO(Product entity) {
        return new ProductDTO(entity.getId(), entity.getName(), entity.getUnitName(), entity.getCounterPrecision());
    }

    public ProductDTO entityIdNameToDTO(Product entity) {
        return new ProductDTO(entity.getId(), entity.getName(), null,  null);
    }

    @Override
    public Product dtoTOEntity(ProductDTO dto) {
        Product entity = new Product();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setUnitName(dto.getUnitName());
        entity.setCounterPrecision(dto.getCounterPrecision());

        return entity;
    }

    @Override
    public Class<?> getEntityClass() {
        return Product.class;
    }
}
