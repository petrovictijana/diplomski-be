package com.tijana.petrovic.diplomski_be.user.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateUserRequest {

    private String firstName;

    private String lastName;

    private String email;
}
