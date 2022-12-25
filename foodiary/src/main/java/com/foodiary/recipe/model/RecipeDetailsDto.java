package com.foodiary.recipe.model;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDetailsDto {
    
    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int recipeId;

    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="게시글 제목", required = true)
    private String title;

    @ApiModelProperty(value="게시글 내용", required = true)
    private String content;

    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private String path;

    @ApiModelProperty(value="게시글 좋아요 수", required = true)
    private int like;

    @ApiModelProperty(value="게시글 조회 수", required = true)
    private int view;

    @ApiModelProperty(value="작성일", required = true)
    private LocalDateTime create;

    @ApiModelProperty(value="댓글 수", required = true)
    private int comment;

    @ApiModelProperty(value="댓글 리스트", required = true)
    List<RecipeCommentDetailsDto> dailyCommentDtoList;
}
