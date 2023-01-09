package com.foodiary.recipe.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientResponseDto {

    @ApiModelProperty(value="재료명", required = false)
    private String ingredient;

    @ApiModelProperty(value="정량", required = false)
    private String dose;
}
