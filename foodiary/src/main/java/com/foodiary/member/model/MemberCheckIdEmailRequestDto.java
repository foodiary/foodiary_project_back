package com.foodiary.member.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCheckIdEmailRequestDto {
 
    @NotBlank(message = "아이디가 비어있습니다")
    @ApiModelProperty(value="사용자 아이디", required = true)
    private String loginId;

    @NotBlank(message = "이메일이 비어있습니다")
    @Email(message = "이메일 형식에 부합하지 않습니다.")
    @ApiModelProperty(value="사용자 이메일", required = true)
    private String email;

}
