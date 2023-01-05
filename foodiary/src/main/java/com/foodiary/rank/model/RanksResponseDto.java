package com.foodiary.rank.model;

import java.util.ArrayList;
import java.util.List;

import com.foodiary.daily.model.DailysResponseDto;
import com.foodiary.recipe.model.RecipesResponseDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RanksResponseDto {
    
    @ApiModelProperty(value="레시피 시퀀스", required = true)
    private int recipeId;
    
    @ApiModelProperty(value="게시글 제목", required = true)
    private String recipeTitle;


    @ApiModelProperty(value="사용자 레시피 공유 게시글")
    private List<RecipesResponseDto> recipeList = new ArrayList<>();

    @ApiModelProperty(value="게시글 직상지", required = true)
    private String recipeWriter;

    @ApiModelProperty(value="게시글 이미지 경로1", required = true)
    private String recipePath1;

    @ApiModelProperty(value="게시글 댓글 수", required = true)
    private int recipeComment;

    @ApiModelProperty(value="게시글 조회 수", required = true)
    private int recipeView;

    @ApiModelProperty(value="게시글 좋아요 수", required = true)
    private int recipeLike;

}
