package com.foodiary.auth.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NewUserResponseDto {

    @ApiModelProperty(value="사용자 이메일", required = true)
    private String email;

    @ApiModelProperty(value="사용자 프로필 사진", required = true)
    private String picture;

    @ApiModelProperty(value="신규유저 판별(true면 신규유저)", required = true)
    private boolean newUser;
}
