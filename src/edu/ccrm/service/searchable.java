package edu.ccrm.service;

import java.util.List;
import java.util.function.Predicate;

/**
 * Interface for searchable entities with functional programming support
 */
public interface Searchable<T> {
    List<T> search(String query);
    List<T> filter(Predicate<T> predicate);
    List<T> findByField(String fieldName, Object value);
    
    // Default method demonstrating interface default methods
    default List<T> searchIgnoreCase(String query) {
        return search(query.toLowerCase());
    }
}
