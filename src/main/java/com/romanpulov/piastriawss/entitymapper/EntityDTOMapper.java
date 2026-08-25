package com.romanpulov.piastriawss.entitymapper;

public interface EntityDTOMapper<E, D> {
    D entityToDTO(E entity);
    E dtoTOEntity(D dto);
    Class<?> getEntityClass();
}
