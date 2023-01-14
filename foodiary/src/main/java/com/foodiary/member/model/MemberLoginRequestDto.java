package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginRequestDto {


    @NotBlank(message = "아이디가 비어있습니다")
    @ApiModelProperty(value="사용자 아이디", required = true)
    private String loginId;

    @NotBlank(message = "비밀번호가 비어있습니다")
    @ApiModelProperty(value="사용자 비밀번호", required = true)
    private String password;
}
