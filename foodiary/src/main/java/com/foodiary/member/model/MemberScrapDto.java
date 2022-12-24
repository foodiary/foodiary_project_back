package com.foodiary.member.model;

import java.util.ArrayList;
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
public class MemberScrapDto {
    
    @ApiModelProperty(value="사용자 하루 식단 게시글")
    private List<DailyDto> dailyList = new ArrayList<>();

    @ApiModelProperty(value="사용자 레시피 공유 게시글")
    private List<RecipeDto> recipeList = new ArrayList<>();
}
