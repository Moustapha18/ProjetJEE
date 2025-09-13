package com.example.location.dao;

import com.example.location.entity.Immeuble;

import java.util.List;

public interface ImmeubleDao extends CrudDao<Immeuble, Long> {
    // src/main/java/com/example/location/dao/ImmeubleDao.java
// ajoute :
    List<Immeuble> findByProprietaireId(Long userId);

}


