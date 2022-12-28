package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetailsDto {
    
    @ApiModelProperty(value="사용자 아이디", required = true)
    private String loginId;
    
    @ApiModelProperty(value="사용자 이메일", required = false)
    private String email;

    @ApiModelProperty(value="사용자 닉네임", required = false)
    private String nickName;

    @ApiModelProperty(value="사용자 소개글", required = false)
    private String profile;

    @ApiModelProperty(value="사용자 이미지 경로", required = false)
    private String path;

}
