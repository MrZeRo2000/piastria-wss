package com.romanpulov.piastriawss.service;

import com.romanpulov.piastriawss.entity.CommonEntity;
import com.romanpulov.piastriawss.exception.CommonEntityNotFoundException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractEntityService<E extends CommonEntity, R extends CrudRepository<E, Long>> implements EntityService<E> {

    protected final R repository;

    public AbstractEntityService(R repository) {
        this.repository = repository;
    }

    protected E getEntityById(Long id) throws CommonEntityNotFoundException {
        return repository.findById(id).orElseThrow(() -> new CommonEntityNotFoundException(id));
    }

    @Override
    public Iterable<E> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public <S extends E> S save(S entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public <S extends E> S insert(S entity) {
        beforeEntityInsert(entity);
        return repository.save(entity);
    }

    protected void beforeEntityInsert(E entity) {

    }

    protected void beforeEntityUpdate(E entity, E savedEntity) {
        entity.setId(savedEntity.getId());
    }

    @Override
    @Transactional
    public <S extends E> S update(Long id, S entity) throws CommonEntityNotFoundException {
        E savedEntity = getEntityById(id);
        beforeEntityUpdate(entity, savedEntity);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws CommonEntityNotFoundException {
        repository.delete(getEntityById(id));
    }
}
