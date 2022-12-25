package com.foodiary.recipe.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeCommentDetailsDto {
 
    @ApiModelProperty(value="댓글 시퀀스", required = true)
    private int commentId;

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int recipeId;

    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="작성자", required = true)
    private String writer;

    @ApiModelProperty(value="내용", required = true)
    private String content;
    
}
