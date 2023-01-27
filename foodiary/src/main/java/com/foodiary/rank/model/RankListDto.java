package com.foodiary.rank.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankListDto {
    
    @ApiModelProperty(value="랭킹 좋아요 수", required = true)
    private int rankDailyLike; 
}
