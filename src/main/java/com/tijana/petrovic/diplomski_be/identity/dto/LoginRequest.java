package com.tijana.petrovic.diplomski_be.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record LoginRequest(
        String email,
        String password
) {}
