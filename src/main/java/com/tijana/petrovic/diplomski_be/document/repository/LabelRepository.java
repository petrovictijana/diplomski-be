package com.tijana.petrovic.diplomski_be.document.repository;

import com.tijana.petrovic.diplomski_be.document.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface LabelRepository extends JpaRepository<Label, UUID> {

    /** Names are stored in their canonical uppercase form, so this is an exact match. */
    boolean existsByName(String name);

    /** Resolves a batch of label names in one statement - callers pass canonical uppercase names. */
    List<Label> findByNameIn(Collection<String> names);
}
