package com.foodiary.member.model;

import java.util.List;

import com.foodiary.daily.model.DailyDto;
import com.foodiary.recipe.model.RecipeDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberPostResponseDto {
    
    @ApiModelProperty(value="데일리 리스트", required = true)
    List<DailyDto> dailyList;
    
    @ApiModelProperty(value="레시피 리스트", required = true)
    List<RecipeDto> recipeList;
}
