package com.example.location.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T t);
    void deleteById(ID id);
}
