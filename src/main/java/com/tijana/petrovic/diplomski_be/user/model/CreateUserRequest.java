package com.tijana.petrovic.diplomski_be.user.model;

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
