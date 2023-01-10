package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRecipeCommentDto {
 
    @ApiModelProperty(value="멤버 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="하루 공유 댓글 시퀀스", required = true)
    private int recipeCommentId;

    @ApiModelProperty(value="하루 공유 시퀀스", required = true)
    private int recipeId;

    @ApiModelProperty(value="하루 공유 제목", required = true)
    private String recipeTitle;

    @ApiModelProperty(value="하루 공유 댓글 작성자", required = true)
    private String recipeCommentWriter;

    @ApiModelProperty(value="하루 공유 댓글 내용", required = true)
    private String recipeCommentBody;

}
