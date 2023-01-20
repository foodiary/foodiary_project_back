package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberPostLikeResponseDto {
    
    @ApiModelProperty(value="스크랩 시퀀스", required = true)
    private int dailyLikeId;

    @ApiModelProperty(value="멤버 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int dailyId;

    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private String dailyThumbnail;

    @ApiModelProperty(value="좋아요한 일자", required = true)
    private String dailyLikeCreate;

}
