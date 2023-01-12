package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberPostScrapResponseDto {
    
    @ApiModelProperty(value="좋아요 시퀀스", required = true)
    private int scrapId;

    @ApiModelProperty(value="멤버 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int postId;

    @ApiModelProperty(value="게시글 제목", required = true)
    private String postTitle;

    @ApiModelProperty(value="게시글 작성자", required = true)
    private String postWriter;

    @ApiModelProperty(value="게시글 작성일자", required = true)
    private String postCreate;
    
    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private String postPath;

    @ApiModelProperty(value="게시글 타입", required = true)
    private String type;

    @ApiModelProperty(value="게시글 댓글 수", required = true)
    private int postComment;

    @ApiModelProperty(value="게시글 조회 수", required = true)
    private int postView;

    @ApiModelProperty(value="게시글 좋아요 수", required = true)
    private int postLike;

    @ApiModelProperty(value="스크랩한 일자", required = true)
    private String scrapCreate;


}
