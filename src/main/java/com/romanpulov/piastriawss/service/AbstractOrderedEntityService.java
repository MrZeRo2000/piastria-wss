package com.romanpulov.piastriawss.service;

import com.romanpulov.piastriawss.entity.CommonEntity;
import com.romanpulov.piastriawss.entity.OrderedEntity;
import com.romanpulov.piastriawss.exception.CommonEntityNotFoundException;
import com.romanpulov.piastriawss.repository.CustomQueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;

public abstract class AbstractOrderedEntityService
        <E extends CommonEntity & OrderedEntity,
        R extends CrudRepository<E, Long>>
        extends AbstractEntityService<E, R>
        implements OrderedEntityService<E>
{

    private final Logger logger = LoggerFactory.getLogger(AbstractOrderedEntityService.class.getName());

    private final CustomQueryRepository customQueryRepository;

    private String getEntityTableName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }

    public AbstractOrderedEntityService(R repository, CustomQueryRepository customQueryRepository) {
        super(repository);
        this.customQueryRepository = customQueryRepository;
    }

    @Override
    protected void beforeEntityInsert(E entity) {
        Long maxOrderId = customQueryRepository.getMaxOrderId(getEntityTableName(entity.getClass()));
        long orderId;
        if (maxOrderId == null) {
            orderId = 1L;
        } else {
            orderId = maxOrderId + 1L;
        }
        entity.setOrderId(orderId);

        super.beforeEntityInsert(entity);
    }

    @Override
    protected void beforeEntityUpdate(E entity, E savedEntity) {
        super.beforeEntityUpdate(entity, savedEntity);
        entity.setOrderId(savedEntity.getOrderId());
    }

    @Transactional
    public int moveOrder(Long fromId, Long toId) throws CommonEntityNotFoundException {
        E fromEntity = getEntityById(fromId);
        E toEntity = getEntityById(toId);

        if ((fromEntity.getOrderId() == null) || (toEntity.getOrderId() == null)) {
            logger.info("Null order id, setting default order");

            customQueryRepository.setDefaultOrder(getEntityTableName(fromEntity.getClass()));

            customQueryRepository.clearEntityManager();

            fromEntity = getEntityById(fromId);
            toEntity = getEntityById(toId);
            
            if ((fromEntity.getOrderId() == null) || (toEntity.getOrderId() == null)) {
                throw new RuntimeException("Error in setting default order");
            }
        }

        logger.info("FromEntity=" + fromEntity + ", ToEntity=" + toEntity);

        if (fromEntity.getOrderId().equals(toEntity.getOrderId())) {
            logger.info("Order is the same, no action");
            return 0;
        } else {
            return customQueryRepository.moveOrder(
                    getEntityTableName(fromEntity.getClass()),
                    fromEntity.getId(),
                    fromEntity.getOrderId(),
                    toEntity.getId(),
                    toEntity.getOrderId()
            );
        }
    }

    @Override
    public int setDefaultOrder(Class<?> entityClass) {
        return customQueryRepository.setDefaultOrder(getEntityTableName(entityClass));
    }
}
