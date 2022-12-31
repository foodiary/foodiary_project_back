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
public class MemberCheckIdRequestDto {
    
    @NotBlank(message = "아이디가 비어있습니다")
    @Pattern(regexp = "^[a-z0-9]{6,13}$", message = "아이디는 6~13자리 영문소문자, 숫자만 가능합니다")
    @ApiModelProperty(value="사용자 아이디", required = false)
    private String loginId;

}
