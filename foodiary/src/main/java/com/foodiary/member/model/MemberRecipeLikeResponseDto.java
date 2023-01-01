package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRecipeLikeResponseDto {
    
    @ApiModelProperty(value="좋아요 시퀀스", required = true)
    private int recipeLikeId;

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int recipeId;

    @ApiModelProperty(value="게시글 제목", required = true)
    private String recipeTitle;

    @ApiModelProperty(value="게시글 작성자", required = true)
    private String recipeWriter;

    @ApiModelProperty(value="게시글 이미지 경로1", required = true)
    private String recipePath1;

    @ApiModelProperty(value="게시글 이미지 경로2", required = true)
    private String recipePath2;

    @ApiModelProperty(value="게시글 이미지 경로3", required = true)
    private String recipePath3;

    @ApiModelProperty(value="게시글 댓글 수", required = true)
    private int recipeComment;

    @ApiModelProperty(value="게시글 조회 수", required = true)
    private int recipeView;

    @ApiModelProperty(value="게시글 좋아요 수", required = true)
    private int recipeLike;
}