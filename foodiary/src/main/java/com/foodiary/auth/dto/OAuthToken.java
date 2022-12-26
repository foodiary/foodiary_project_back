package com.foodiary.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OAuthToken {
    
    private String accessToken;
    private String expiresIn;
    private String idToken;
    private String refreshToken;
    private String scope;
    private String tokenType;
}
