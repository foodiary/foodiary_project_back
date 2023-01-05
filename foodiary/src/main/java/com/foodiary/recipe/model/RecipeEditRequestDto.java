package com.foodiary.recipe.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeEditRequestDto {

    @Setter
    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int recipeId;

    @Setter
    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;
    
    @ApiModelProperty(value="게시글 제목", required = true)
    private String title;

    @ApiModelProperty(value="게시글 내용", required = true)
    private String content;

    @ApiModelProperty(value="이미지 경로1", required = true)
    @Setter private String path1;

    @ApiModelProperty(value="이미지 경로2", required = true)
    @Setter private String path2;

    @ApiModelProperty(value="이미지 경로3", required = true)
    @Setter private String path3;
}
