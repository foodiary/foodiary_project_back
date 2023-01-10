package com.foodiary.recipe.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class IngredientRequestDto {

    @Setter
    @ApiModelProperty(value="레시피 시퀀스", required = false)
    private int recipeId;

    @ApiModelProperty(value="재료명", required = false)
    private String ingredient;

    @ApiModelProperty(value="정량", required = false)
    private String dose;
}
