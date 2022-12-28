package com.foodiary.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GoogleUserDto {
    public String id;
    public String email;
    public Boolean verifiedEmail;
    public String name;
    public String givenName;
    public String familyName;
    public String picture;
    public String locale;


}