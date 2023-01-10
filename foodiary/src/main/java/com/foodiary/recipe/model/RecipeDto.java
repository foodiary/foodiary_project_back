package com.foodiary.recipe.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {
    
    @ApiModelProperty(value="멤버 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int recipeId;

    @ApiModelProperty(value="게시글 제목", required = true)
    private String recipeTitle;

    @ApiModelProperty(value="게시글 작성자", required = true)
    private String recipeWriter;

    @ApiModelProperty(value="게시글 작성일자", required = true)
    private String recipeCreate;

    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private String recipePath;

    @ApiModelProperty(value="게시글 댓글 수", required = true)
    private int recipeComment;

    @ApiModelProperty(value="게시글 조회 수", required = true)
    private int recipeView;

    @ApiModelProperty(value="게시글 좋아요 수", required = true)
    private int recipeLike;

    @ApiModelProperty(value="게시글이 없을 경우", required = true)
    private String no;
    
    public void noAdd() {
        no = "게시글이 없습니다.";
    }

}
