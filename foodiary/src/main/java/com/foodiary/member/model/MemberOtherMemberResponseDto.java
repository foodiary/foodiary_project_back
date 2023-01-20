package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberOtherMemberResponseDto {
    
    @ApiModelProperty(value="멤버 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int dailyId;

    @ApiModelProperty(value="게시글 작성일자", required = true)
    private String dailyCreate;

    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private String dailyThumbnail;

    @ApiModelProperty(value="사용자 닉네임", required = true)
    private String memberNickName;

    @ApiModelProperty(value="사용자 소개글", required = false)
    private String memberProfile;

    @ApiModelProperty(value="사용자 이미지 경로", required = false)
    private String memberPath;
}
