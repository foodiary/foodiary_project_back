package com.foodiary.auth.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenReissueDto {

    private int memberId;

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private LocalDateTime accessTokenExpirationMinutes;

    private LocalDateTime refreshTokenExpirationMinutes;
}
