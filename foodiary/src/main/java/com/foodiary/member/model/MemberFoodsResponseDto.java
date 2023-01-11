package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberFoodsResponseDto {
    
    @ApiModelProperty(value="추천받은 음식 시퀀스", required = true)
    private int memberFoodId;

    @ApiModelProperty(value="사용자 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="음식 시퀀스", required = true)
    private int foodId;

    @ApiModelProperty(value="음식 이름", required = true)
    private String foodName;

    @ApiModelProperty(value="음식 좋아요 상태", required = true)
    private String memberFoodLike;

    @ApiModelProperty(value="음식 싫어요 상태", required = true)
    private String memberFoodHate;

}
