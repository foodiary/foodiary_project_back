package com.foodiary.search.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 리스펀스, 리퀘스트 둘다 사용
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponseMemberDto {
    
    @ApiModelProperty(value="사용자 시퀀스", required = false)
    private int memberId;
    
    @ApiModelProperty(value="키워드 시퀀스", required = true)
    private int keywordId;

    @ApiModelProperty(value="키워드", required = true)
    private String keyword;

}
