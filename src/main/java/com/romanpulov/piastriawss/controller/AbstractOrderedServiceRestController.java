package com.romanpulov.piastriawss.controller;

import com.romanpulov.piastriawss.dto.RowsAffectedDTO;
import com.romanpulov.piastriawss.entity.CommonEntity;
import com.romanpulov.piastriawss.entity.OrderedEntity;
import com.romanpulov.piastriawss.entitymapper.EntityDTOMapper;
import com.romanpulov.piastriawss.exception.CommonEntityNotFoundException;
import com.romanpulov.piastriawss.service.OrderedEntityService;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public abstract class AbstractOrderedServiceRestController<E extends CommonEntity & OrderedEntity, D, S extends OrderedEntityService<E>>
        extends AbstractServiceRestController <E, D, S> {

    public AbstractOrderedServiceRestController(S entityService, EntityDTOMapper<E, D> mapper, Logger logger) {
        super(entityService, mapper, logger);
    }

    @PostMapping(value="operation:move_order")
    ResponseEntity<RowsAffectedDTO> moveOrder (
            @RequestParam("fromId")
                    Long fromId,
            @RequestParam("toId")
                    Long toId
    )  throws CommonEntityNotFoundException {
        return ResponseEntity.ok(new RowsAffectedDTO(entityService.moveOrder(fromId, toId)));
    }

    @PostMapping(value="operation:set_default_order")
    ResponseEntity<RowsAffectedDTO> setDefaultOrder ()  {
        return ResponseEntity.ok(new RowsAffectedDTO(entityService.setDefaultOrder(mapper.getEntityClass())));
    }
}
