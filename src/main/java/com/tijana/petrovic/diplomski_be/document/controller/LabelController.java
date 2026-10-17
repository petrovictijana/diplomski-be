package com.tijana.petrovic.diplomski_be.document.controller;

import com.tijana.petrovic.diplomski_be.document.dto.CreateLabelRequest;
import com.tijana.petrovic.diplomski_be.document.dto.LabelResponse;
import com.tijana.petrovic.diplomski_be.document.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/labels")
public class LabelController {

    private final LabelService labelService;

    /** The labels the authenticated caller may read, given their effective permissions. */
    @GetMapping
    public List<LabelResponse> getVisibleLabels() {
        return labelService.getVisibleLabels();
    }

    @PostMapping
    public ResponseEntity<LabelResponse> createLabel(@Valid @RequestBody CreateLabelRequest request) {
        var label = labelService.createLabel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(label);
    }

}
