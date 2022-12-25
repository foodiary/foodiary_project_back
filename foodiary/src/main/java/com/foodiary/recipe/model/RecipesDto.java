package com.foodiary.recipe.model;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipesDto {

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int recipeId;

    @ApiModelProperty(value="게시글 제목", required = true)
    private String title;

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

}
