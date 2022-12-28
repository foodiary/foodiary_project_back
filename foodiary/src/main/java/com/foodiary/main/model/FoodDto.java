package com.foodiary.main.model;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FoodDto {
    
    @ApiModelProperty(value="추천 음식", required = true)
    private String food;

    @ApiModelProperty(value="추천 음식 카테고리", required = true)
    private String foodCategory;

    @ApiModelProperty(value="추천일", required = true)
    private LocalDateTime day;
    
}
