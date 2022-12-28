package com.foodiary.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OAuthTokenDto {
    
    private String accessToken;
    private String expiresIn;
    private String idToken;
    private String refreshToken;
    private String scope;
    private String tokenType;
}
