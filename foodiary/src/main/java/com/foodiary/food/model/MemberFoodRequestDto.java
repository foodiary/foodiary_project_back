package com.foodiary.food.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberFoodRequestDto {

    @Setter
    @ApiModelProperty(value="추천받은 음식 시퀀스", required = true)
    private int memberFoodId;

    @ApiModelProperty(value="사용자 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="음식 시퀀스", required = false)
    private int foodId;
}
