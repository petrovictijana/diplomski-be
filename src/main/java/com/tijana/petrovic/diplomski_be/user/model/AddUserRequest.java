package com.tijana.petrovic.diplomski_be.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddUserRequest {

    private String firstName;

    private String lastName;

    private String email;
}
