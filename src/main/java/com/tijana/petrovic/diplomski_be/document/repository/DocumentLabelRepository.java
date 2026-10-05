package com.tijana.petrovic.diplomski_be.document.repository;

import com.tijana.petrovic.diplomski_be.document.entity.DocumentLabel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentLabelRepository extends JpaRepository<DocumentLabel, UUID> {
}
