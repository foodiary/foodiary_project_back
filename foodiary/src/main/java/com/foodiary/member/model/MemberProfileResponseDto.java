package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberProfileResponseDto {
    
    @ApiModelProperty(value="사용자 닉네임", required = true)
    private String memberNickName;

    @ApiModelProperty(value="사용자 소개글", required = false)
    private String memberProfile;

    @ApiModelProperty(value="사용자 이미지 경로", required = false)
    private String memberPath;
}
