package com.tijana.petrovic.diplomski_be.access.controller;

import com.tijana.petrovic.diplomski_be.access.dto.GrantPermissionRequest;
import com.tijana.petrovic.diplomski_be.access.dto.PermissionGrantResponse;
import com.tijana.petrovic.diplomski_be.access.entity.ResourceType;
import com.tijana.petrovic.diplomski_be.access.service.PermissionGrantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/permissions")
public class PermissionGrantController {

    private final PermissionGrantService permissionGrantService;

    @PostMapping
    public ResponseEntity<PermissionGrantResponse> grant(@Valid @RequestBody GrantPermissionRequest request) {
        var grant = permissionGrantService.grant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(grant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revoke(@PathVariable UUID id) {
        permissionGrantService.revoke(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PermissionGrantResponse>> listForResource(
            @RequestParam ResourceType resourceType,
            @RequestParam UUID resourceId) {
        return ResponseEntity.ok(permissionGrantService.listForResource(resourceType, resourceId));
    }
}
