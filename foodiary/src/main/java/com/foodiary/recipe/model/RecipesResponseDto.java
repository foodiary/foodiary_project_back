package com.foodiary.recipe.model;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipesResponseDto {

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int recipeId;

    @ApiModelProperty(value="게시글 제목", required = true)
    private String recipeTitle;

    @ApiModelProperty(value="게시글 작성자", required = true)
    private String recipeWriter;

    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private String recipePath1;

    @ApiModelProperty(value="게시글 좋아요 수", required = true)
    private int recipeLike;

    @ApiModelProperty(value="게시글 조회 수", required = true)
    private int view;

    @ApiModelProperty(value="작성일", required = true)
    private LocalDateTime recipeCreate;

    @ApiModelProperty(value="댓글 수", required = true)
    private int recipeComment;

}
