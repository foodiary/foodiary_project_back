package com.foodiary.member.model;

import java.util.ArrayList;
import java.util.List;

import com.foodiary.daily.model.DailyDto;
import com.foodiary.recipe.model.RecipeDto;

public class MemberSerchDto {
    
    // 그사람이 쓴 게시글, 프로필 이미지, 프로필 내용, 좋아요 받은수

    List<DailyDto> dailyList = new ArrayList<>();

    List<RecipeDto> recipeList = new ArrayList<>();

}
