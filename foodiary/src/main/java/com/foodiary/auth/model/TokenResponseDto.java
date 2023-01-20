package com.foodiary.auth.model;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TokenResponseDto {

    @ApiModelProperty(value="memberId", required = true)
    private int memberId;

    @ApiModelProperty(value="Access Token", required = true)
    private String accessToken;

    @ApiModelProperty(value="Refresh Token", required = true)
    private String refreshToken;

    @ApiModelProperty(value="토큰 타입", required = true)
    private String tokenType;

    @ApiModelProperty(value="Access Token 만료시간", required = true)
    private LocalDateTime accessTokenExpirationMinutes;

    @ApiModelProperty(value="RefreshToken 만료시간", required = true)
    private LocalDateTime refreshTokenExpirationMinutes;

    @ApiModelProperty(value="신규유저 판별(true면 신규유저)", required = true)
    private boolean newUser;

    public TokenResponseDto(String accessToken, String refreshRoken, String tokenType, boolean newUser) {
        this.accessToken = accessToken;
        this.refreshToken = refreshRoken;
        this.tokenType = tokenType;
        this.newUser = newUser;
    }
}
