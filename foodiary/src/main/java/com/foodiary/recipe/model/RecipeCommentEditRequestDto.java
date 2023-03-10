package com.foodiary.recipe.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeCommentEditRequestDto {

    @Setter
    @ApiModelProperty(value="댓글 시퀀스", required = true)
    private int commentId;

    @Setter
    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int recipeId;

    @Setter
    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="내용", required = true)
    private String content;

}
