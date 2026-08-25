package com.romanpulov.piastriawss.service;

import com.romanpulov.piastriawss.entity.CommonEntity;
import com.romanpulov.piastriawss.exception.CommonEntityNotFoundException;

import java.util.Optional;

public interface EntityService<E extends CommonEntity> {
    Iterable<E> findAll();
    <S extends E> S save(S entity) ;
    <S extends E> S insert(S entity) ;
    <S extends E> S update(Long id, S entity) throws CommonEntityNotFoundException;
    void deleteById(Long id) throws CommonEntityNotFoundException;
}

