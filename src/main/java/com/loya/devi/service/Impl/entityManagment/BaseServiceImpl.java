package com.loya.devi.service.Impl.entityManagment;

import com.loya.devi.controller.request.Filter;
import com.loya.devi.exception.ViolationException;
import com.loya.devi.service.interfaces.BaseService;
import com.loya.devi.service.interfaces.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * The generic class is engaged in managing entities in the database.
 *
 * @author DEVIAPHAN on 21.12.2018
 * @project university
 */
@Service
public class BaseServiceImpl<T> implements BaseService<T> {

    public static final String VIOLATION_EXCEPTION = "ViolationException";
    @Autowired
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

    private static final String ENTITY_NOT_FOUND_EXCEPTION = "EntityNotFoundException";
    private static final String CAN_NOT_FIND_WITH_ID = "Can_not_find_with_id";

    /**
     * method return all entities
     *
     * @param function of desired method in service
     * @param locale   of message
     * @param message  name of message
     * @param par1     of message
     * @return list of all entities
     */
    @Override
    public List<T> getAll(Supplier<List<T>> function, Locale locale, String message, String par1) {
        LOGGER.info(messageSource.getMessage(message, new Object[]{par1}, locale));
        return function.get();
    }

    /**
     * method return a entity by id
     *
     * @param id       of entity
     * @param function of desired method in service
     * @param locale   of message
     * @param message  name of message
     * @param par1     of message
     * @param excPar1  text for exception message
     * @return entity by id
     */
    public T get(Long id, Function<Long, Optional<T>> function, Locale locale, String message, String par1, String excPar1) {
        Optional<T> optional = function.apply(id);
        if (optional.isPresent()) {
            LOGGER.info(messageSource.getMessage(message, new Object[]{par1, id}, locale));
            return optional.get();
        } else {
            LOGGER.error(messageSource.getMessage(ENTITY_NOT_FOUND_EXCEPTION, new Object[]{par1, id}, locale));
            throw new EntityNotFoundException(messageSource.getMessage(CAN_NOT_FIND_WITH_ID, new Object[]{id}, locale));
        }
    }


    /**
     * method return all entities by id
     *
     * @param id       of entity
     * @param function of desired method in service
     * @param locale   of message
     * @param message  name of message
     * @param par1     of message
     * @param excPar1  text for exception message
     * @return list of all entities
     */
    @Override
    public List<T> getAll(Long id, Function<Long, List<T>> function, Locale locale, String message, String par1, String excPar1) {
        List<T> list = function.apply(id);
        if (list.isEmpty()) {
            LOGGER.error(messageSource.getMessage(ENTITY_NOT_FOUND_EXCEPTION, new Object[]{par1, id}, locale));
            throw new EntityNotFoundException();
        } else {
            LOGGER.info(messageSource.getMessage(message, new Object[]{par1, id}, locale));
            return list;
        }
    }

    /**
     * method save entity and return it
     *
     * @param entity   stored object
     * @param function of desired method in service
     * @param locale   of message
     * @param message  name of message
     * @param par1     of message
     * @return entity
     */
    @Override
    @Transactional
    public T save(T entity, UnaryOperator<T> function, Locale locale, String message, String par1) {
        T returnEntity = null;
        try {
            returnEntity = function.apply(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new ViolationException(messageSource.getMessage(VIOLATION_EXCEPTION, new Object[]{}, locale));
        }
        LOGGER.info(messageSource.getMessage(message, new Object[]{par1}, locale));
        return returnEntity;
    }

    /**
     * method save entity and return it
     *
     * @param entity   stored object
     * @param function of desired method in service
     * @param locale   of message
     * @param message  name of message
     * @param par1     of message
     * @param par2     of message
     * @return entity
     */
    @Override
    @Transactional
    public T save(T entity, UnaryOperator<T> function, Locale locale, String message, String par1, Long par2) {
        LOGGER.info(messageSource.getMessage(message, new Object[]{par1, par2}, locale));
        return function.apply(entity);
    }

    /**
     * method save all entities
     *
     * @param entities stored objects
     * @param function of desired method in service
     * @param locale   of message
     * @param message  name of message
     * @param par1     of message
     */
    @Override
    @Transactional
    public void saveAll(List<T> entities, Consumer<List<T>> function, Locale locale, String message, String
            par1) {
        LOGGER.info(messageSource.getMessage(message, new Object[]{par1}, locale));
        function.accept(entities);
    }

    /**
     * method delete entity by id
     *
     * @param id       of entity
     * @param function of desired method in service
     * @param locale   of message
     * @param message  name of message
     * @param par1     of message
     */
    @Override
    public void deleteById(Long id, Consumer<Long> function, Locale locale, String message, String par1) {
        LOGGER.info(messageSource.getMessage(message, new Object[]{par1, id}, locale));
        function.accept(id);
    }

    /**
     * method takes parameters, lambdas and result entity as result of filtering or list of entities as result of pagination
     *
     * @param filter        is obj which stores the strings for pagination
     * @param pageable      is obj which store Pageable
     * @param getByFilter   is supplier
     * @param getByPageSupp is supplier
     * @return list of entities
     */
    @Override
    public Page<T> getByFilter(Filter filter, Pageable pageable, Supplier<Page<T>> getByFilter, Supplier<Page<T>> getByPageSupp) {
        return filter.isFilterExist() ? getByFilter.get() : getByPageSupp.get();
    }
}
