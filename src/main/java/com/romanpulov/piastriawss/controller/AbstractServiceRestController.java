package com.romanpulov.piastriawss.controller;

import com.romanpulov.piastriawss.entity.CommonEntity;
import com.romanpulov.piastriawss.entitymapper.EntityDTOMapper;
import com.romanpulov.piastriawss.exception.CommonEntityNotFoundException;
import com.romanpulov.piastriawss.service.EntityService;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractServiceRestController<E extends CommonEntity, D, S extends EntityService<E>> {

    protected final S entityService;
    protected final EntityDTOMapper<E, D> mapper;
    protected final Logger logger;

    public AbstractServiceRestController(S entityService, EntityDTOMapper<E, D> mapper, Logger logger) {
        this.entityService = entityService;
        this.mapper = mapper;
        this.logger = logger;
    }

    @GetMapping
    ResponseEntity<List<D>> all() {
        List<D> result = new ArrayList<>();
        this.entityService.findAll().forEach(paymentObject -> result.add(mapper.entityToDTO(paymentObject)));

        return ResponseEntity.ok(result);
    }

    @PostMapping
    ResponseEntity<D> post(@RequestBody D dto) {
        E entity = mapper.dtoTOEntity(dto);
        return ResponseEntity.ok(mapper.entityToDTO(entityService.insert(entity)));
    }

    @PutMapping("/{id}")
    ResponseEntity<D> put(@PathVariable Long id, @RequestBody D dto) throws CommonEntityNotFoundException {
        try {
            E updatedEntity = mapper.dtoTOEntity(dto);
            return ResponseEntity.ok(mapper.entityToDTO(entityService.update(id, updatedEntity)));
        } catch (CommonEntityNotFoundException e) {
            logger.error("Entity with id=" + id + " does not exist");
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws CommonEntityNotFoundException {
        try {
            entityService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (CommonEntityNotFoundException e) {
            logger.error("Entity with id=" + id + " does not exist");
            throw e;
        }
    }
}
