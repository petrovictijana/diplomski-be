package com.tijana.petrovic.diplomski_be.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivateAccountRequest {

    private String token;

    private String password;
}
