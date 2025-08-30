package com.example.location.dao;

import java.util.List;
import java.util.Optional;

/**
 * ISP: petite interface spécifique CRUD.
 * LSP: impl doivent respecter le contrat (ex: retourner Optional.empty si absent).
 */
public interface CrudDao<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T t);
    void deleteById(ID id);
}
