package com.foodiary.member.model;

import java.util.ArrayList;
import java.util.List;

import com.foodiary.daily.model.DailysResponseDto;
import com.foodiary.recipe.model.RecipesDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSerchResponseDto {
    
    // 그사람이 쓴 게시글, 프로필 이미지, 프로필 내용, 좋아요 받은수
    @ApiModelProperty(value="사용자 하루 식단 게시글")
    private List<DailysResponseDto> dailyList = new ArrayList<>();

    @ApiModelProperty(value="사용자 레시피 공유 게시글")
    private List<RecipesDto> recipeList = new ArrayList<>();

    @ApiModelProperty(value="사용자 소개글")
    private String profile;

    @ApiModelProperty(value="사용자 이미지 경로")
    private String path;

    @ApiModelProperty(value="사용자 좋아요 수")
    private int like;
}
