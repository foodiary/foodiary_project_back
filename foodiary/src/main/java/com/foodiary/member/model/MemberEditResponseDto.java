package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberEditResponseDto {
    
    @ApiModelProperty(value="사용자 시퀀스", required = false, hidden = true)
    private int memberId;

    @ApiModelProperty(value="사용자 아이디", required = true)
    private String memberLoginId;

    @ApiModelProperty(value="사용자 이메일", required = true)
    private String memberEmail;
    
    @ApiModelProperty(value="사용자 비밀번호", required = true)
    private String memberPassword;

    @ApiModelProperty(value="사용자 닉네임", required = true)
    private String memberNickName;

    @ApiModelProperty(value="사용자 이미지 경로", required = false)
    private String memberPath;

    @ApiModelProperty(value="사용자 소개글", required = false)
    private String memberProfile;
}
