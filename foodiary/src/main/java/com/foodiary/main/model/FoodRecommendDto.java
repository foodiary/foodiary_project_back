package com.foodiary.main.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FoodRecommendDto {

    @ApiModelProperty(value="월요일 점심 카테고리", required = true)
    private String MonLunchCategory;

    @ApiModelProperty(value="월요일 점심 추천 음식", required = true)
    private String MonLunchfood;

    @ApiModelProperty(value="월요일 저녁 카테고리", required = true)
    private String MonDinnerCategory;

    @ApiModelProperty(value="월요일 저녁 추천 음식", required = true)
    private String MonDinnerfood;

    @ApiModelProperty(value="화요일 점심 카테고리", required = true)
    private String TueLunchCategory;

    @ApiModelProperty(value="화요일 점심 추천 음식", required = true)
    private String TueLunchfood;

    @ApiModelProperty(value="화요일 저녁 카테고리", required = true)
    private String TueDinnerCategory;

    @ApiModelProperty(value="화요일 저녁 추천 음식", required = true)
    private String TueDinnerfood;

    @ApiModelProperty(value="수요일 점심 카테고리", required = true)
    private String WedLunchCategory;

    @ApiModelProperty(value="수요일 점심 추천 음식", required = true)
    private String WedLunchfood;

    @ApiModelProperty(value="수요일 저녁 카테고리", required = true)
    private String WedDinnerCategory;

    @ApiModelProperty(value="수요일 저녁 추천 음식", required = true)
    private String WedDinnerfood;

    @ApiModelProperty(value="목요일 점심 카테고리", required = true)
    private String ThuLunchCategory;

    @ApiModelProperty(value="목요일 점심 추천 음식", required = true)
    private String ThuLunchfood;

    @ApiModelProperty(value="목요일 저녁 카테고리", required = true)
    private String ThuDinnerCategory;

    @ApiModelProperty(value="목요일 저녁 추천 음식", required = true)
    private String ThuDinnerfood;

    @ApiModelProperty(value="금요일 점심 카테고리", required = true)
    private String FriLunchCategory;

    @ApiModelProperty(value="금요일 점심 추천 음식", required = true)
    private String FriLunchfood;

    @ApiModelProperty(value="금요일 저녁 카테고리", required = true)
    private String FriDinnerCategory;

    @ApiModelProperty(value="금요일 저녁 추천 음식", required = true)
    private String FriDinnerfood;

    @ApiModelProperty(value="토요일 점심 카테고리", required = true)
    private String SatLunchCategory;

    @ApiModelProperty(value="토요일 점심 추천 음식", required = true)
    private String SatLunchfood;

    @ApiModelProperty(value="토요일 저녁 카테고리", required = true)
    private String SatDinnerCategory;

    @ApiModelProperty(value="토요일 저녁 추천 음식", required = true)
    private String SatDinnerfood;

    @ApiModelProperty(value="일요일 점심 카테고리", required = true)
    private String SunLunchCategory;

    @ApiModelProperty(value="일요일 점심 추천 음식", required = true)
    private String SunLunchfood;

    @ApiModelProperty(value="일요일 저녁 카테고리", required = true)
    private String SunDinnerCategory;

    @ApiModelProperty(value="일요일 저녁 추천 음식", required = true)
    private String SunDinnerfood;

}
