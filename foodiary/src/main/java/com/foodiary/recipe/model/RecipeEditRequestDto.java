package com.foodiary.recipe.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    @ApiModelProperty(value="레시피 재료", required = true)
    private List<IngredientRequestDto> ingredients;

    @ApiModelProperty(value="이미지 경로1", required = true)
    @Setter private String path1;

    @ApiModelProperty(value="이미지 경로2", required = true)
    @Setter private String path2;

    @ApiModelProperty(value="이미지 경로3", required = true)
    @Setter private String path3;

    @ApiModelProperty(value="이미지1 변경 유무", required = true)
    @Setter private String path1YN;

    @ApiModelProperty(value="이미지2 변경 유무", required = true)
    @Setter private String path2YN;

    @ApiModelProperty(value="이미지3 변경 유무", required = true)
    @Setter private String path3YN;
}
