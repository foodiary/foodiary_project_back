package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCheckEmailNumRequestDto {
    
    @ApiModelProperty(value="사용자 이메일", required = true)
    private String email;

    @ApiModelProperty(value="인증번호", required = true)
    private String num;
}
