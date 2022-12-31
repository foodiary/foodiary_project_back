package com.foodiary.food.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@Builder
public class FoodRecommendResponseDto {

    @ApiModelProperty(value="음식 시퀀스", required = true)
    private int foodId;

    @ApiModelProperty(value="음식 이름", required = true)
    private String foodName;

    @ApiModelProperty(value="음식 카테고리", required = true)
    private String foodCategory;

    public FoodRecommendResponseDto(int foodId, String foodName, String foodCategory) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodCategory = foodCategory;
    }

}
