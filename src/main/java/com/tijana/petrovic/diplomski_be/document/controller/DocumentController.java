package com.tijana.petrovic.diplomski_be.document.controller;

import com.tijana.petrovic.diplomski_be.document.dto.CreateDocumentRequest;
import com.tijana.petrovic.diplomski_be.document.dto.DocumentLabelsResponse;
import com.tijana.petrovic.diplomski_be.document.dto.DocumentUploadResponse;
import com.tijana.petrovic.diplomski_be.document.dto.UpdateDocumentLabelsRequest;
import com.tijana.petrovic.diplomski_be.document.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentUploadResponse> createDocument(@Valid @RequestBody CreateDocumentRequest request) {
        var document = documentService.createDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @PutMapping("/{documentId}/labels")
    public ResponseEntity<DocumentLabelsResponse> updateLabels(
            @PathVariable UUID documentId,
            @Valid @RequestBody UpdateDocumentLabelsRequest request) {
        var response = documentService.updateLabels(documentId, request);
        return ResponseEntity.ok(response);
    }

}
