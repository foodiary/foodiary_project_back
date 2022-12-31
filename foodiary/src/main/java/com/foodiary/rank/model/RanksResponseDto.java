package com.foodiary.rank.model;

import java.util.ArrayList;
import java.util.List;

import com.foodiary.daily.model.DailysResponseDto;
import com.foodiary.recipe.model.RecipesDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RanksResponseDto {
    
    @ApiModelProperty(value="사용자 하루 식단 게시글")
    private List<DailysResponseDto> dailyList = new ArrayList<>();

    @ApiModelProperty(value="사용자 레시피 공유 게시글")
    private List<RecipesDto> recipeList = new ArrayList<>();
    
}
