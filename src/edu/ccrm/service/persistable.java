package edu.ccrm.service;

import java.util.List;
import java.util.Optional;

/**
 * Generic interface demonstrating generics and functional interfaces
 */
public interface Persistable<T, ID> {
    void save(T entity);
    void update(T entity);
    void delete(ID id);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean exists(ID id);
    long count();
}
