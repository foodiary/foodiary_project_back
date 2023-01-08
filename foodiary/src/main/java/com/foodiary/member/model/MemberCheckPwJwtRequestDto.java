package com.foodiary.member.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCheckPwJwtRequestDto {
    
    @NotBlank(message = "비밀번호가 비어있습니다")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,16}$", message = "비밀번호는 8~16자리 영문자, 숫자, 특수문자를 포함해야합니다.")
    @ApiModelProperty(value="사용자 비밀번호", required = true)
    private String password;

    @NotBlank(message = "비밀번호를 한번 더 입력하세요")
    @ApiModelProperty(value="사용자 비밀번호 한번더 입력", required = true)
    private String more_password;

    @ApiModelProperty(value="사용자 토큰값", required = true)
    private String jwt;

}

