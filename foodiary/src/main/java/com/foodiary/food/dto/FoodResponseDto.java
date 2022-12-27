package com.foodiary.food.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class FoodResponseDto {
    private String foodName;


    public FoodResponseDto(String foodName){
        this.foodName = foodName;
    }
}
