package com.foodiary.auth.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private LocalDateTime accessTokenExpirationMinutes;
    private LocalDateTime refreshTokenExpirationMinutes;
    private boolean newUser;

    public TokenResponse(String accessToken, String refreshRoken, String tokenType, boolean newUser) {
        this.accessToken = accessToken;
        this.refreshToken = refreshRoken;
        this.tokenType = tokenType;
        this.newUser = newUser;
    }
}
