package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginDto {
    
    @ApiModelProperty(value="사용자 아이디", required = true)
    private String loginId;

    @ApiModelProperty(value="사용자 비밀번호", required = true)
    private String password;
}
