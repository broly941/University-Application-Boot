package com.loya.devi.service.interfaces;

import com.loya.devi.controller.request.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @author DEVIAPHAN on 21.12.2018
 * @project university
 */
public interface BaseService<T> {
    T get(Long id, Function<Long, Optional<T>> function, Locale locale, String message, String par1, String excPar1);

    List<T> getAll(Supplier<List<T>> function, Locale locale, String message, String par1);

    List<T> getAll(Long id, Function<Long, List<T>> function, Locale locale, String message, String par1, String excPar1);

    T save(T entity, UnaryOperator<T> function, Locale locale, String message, String par1);

    T save(T entity, UnaryOperator<T> function, Locale locale, String message, String par1, Long par2);

    void saveAll(List<T> entities, Consumer<List<T>> function, Locale locale, String message, String par1);

    void deleteById(Long id, Consumer<Long> function, Locale locale, String message, String par1);

    Page<T> getByFilter(Filter filter, Pageable pageable, Supplier<Page<T>> getByFilter, Supplier<Page<T>> getByPageSupp);
}
