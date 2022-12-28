package com.foodiary.food.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class FoodResponseDto {

    private int foodId;

    private String foodName;

    private String foodCategory;

    public FoodResponseDto(int foodId, String foodName, String foodCategory) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodCategory = foodCategory;
    }

}
