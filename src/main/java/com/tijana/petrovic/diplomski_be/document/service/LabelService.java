package com.tijana.petrovic.diplomski_be.document.service;

import com.tijana.petrovic.diplomski_be.document.dto.CreateLabelRequest;
import com.tijana.petrovic.diplomski_be.document.dto.LabelResponse;
import com.tijana.petrovic.diplomski_be.document.entity.Label;
import com.tijana.petrovic.diplomski_be.document.exception.LabelAlreadyExistsException;
import com.tijana.petrovic.diplomski_be.document.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Log4j2
@RequiredArgsConstructor
@Service
public class LabelService {

    private final LabelRepository labelRepository;

    @Transactional
    public LabelResponse createLabel(CreateLabelRequest request) {
        // Locale.ROOT, not the default locale: under a Turkish locale "big".toUpperCase()
        // yields "BİG", which Postgres upper() does not produce, so the row would be
        // rejected by ck_Label_name_uppercase on some machines and accepted on others.
        var name = request.name().toUpperCase(Locale.ROOT);

        if (labelRepository.existsByName(name)) {
            throw new LabelAlreadyExistsException("A label named '%s' already exists.".formatted(name));
        }

        var label = Label.builder()
                .name(name)
                .build();

        try {
            // saveAndFlush, so the insert happens inside this try block - with a client
            // assigned UUID the statement would otherwise be deferred to commit, and the
            // unique violation would surface outside the catch.
            var saved = labelRepository.saveAndFlush(label);

            log.info("[LabelService] Created label {} ({})", saved.getName(), saved.getId());
            return LabelResponse.from(saved);
        } catch (DataIntegrityViolationException exception) {
            // The unique index is the real guard - two concurrent requests both pass the
            // check above, and only one of them can reach the database.
            throw new LabelAlreadyExistsException("A label named '%s' already exists.".formatted(name));
        }
    }

}
