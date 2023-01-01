package com.foodiary.search.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestDto {
    
    @ApiModelProperty(value="사용자 시퀀스", required = false)
    private int memberId;

    @ApiModelProperty(value="검색어", required = true)
    private String keyword;

}
