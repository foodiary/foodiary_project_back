package com.foodiary.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NewUserResponseDto {

    private String email;
    private String picture;
    private boolean newUser;
}
