package org.delcom.ifs25042pbo2026springbootcashflow.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Generic Repository Port: CRUD dan pencarian.
 *
 * @param <T>  tipe entitas domain
 * @param <ID> tipe identifier entitas
 */
public interface IRepository<T, ID> {
    List<T> findAll();

    Optional<T> findById(ID id);

    T save(T entity);

    /** @return true jika ada data yang terhapus */
    boolean deleteById(ID id);

    void update(T entity);

    List<T> findBy(Predicate<T> predicate);
}
