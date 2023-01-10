package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDailyCommentDetailResponseDto {
 
    @ApiModelProperty(value="멤버 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="하루 공유 댓글 시퀀스", required = true)
    private int dailyCommentId;

    @ApiModelProperty(value="하루 공유 게시글 시퀀스", required = true)
    private int dailyId;

    @ApiModelProperty(value="하루 공유 제목", required = true)
    private String dailyTitle;

    @ApiModelProperty(value="하루 공유 댓글 작성자", required = true)
    private String dailyCommentWriter;

    @ApiModelProperty(value="하루 공유 댓글 내용", required = true)
    private String dailyCommentBody;

    @ApiModelProperty(value="하루 공유 댓글 작성일자", required = true)
    private String dailyCommentCreate;

    @ApiModelProperty(value="하루 공유 좋아요 수", required = true)
    private int dailyLike;

    @ApiModelProperty(value="하루 공유 스크랩 수", required = true)
    private int dailyScrap;
}
