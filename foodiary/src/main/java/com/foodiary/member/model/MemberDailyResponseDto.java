package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDailyResponseDto {
    
    @ApiModelProperty(value="멤버 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int dailyId;

    @ApiModelProperty(value="게시글 작성일자", required = true)
    private String dailyCreate;

    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private String dailyPath;
    
}
