package com.romanpulov.piastriawss.service;

import com.romanpulov.piastriawss.entity.CommonEntity;
import com.romanpulov.piastriawss.exception.CommonEntityNotFoundException;

public interface OrderedEntityService<E extends CommonEntity> extends EntityService<E> {
    int moveOrder(Long fromId, Long toId) throws CommonEntityNotFoundException;
    int setDefaultOrder(Class<?> entityClass);
}
