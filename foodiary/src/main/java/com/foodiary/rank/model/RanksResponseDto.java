package com.foodiary.rank.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RanksResponseDto {
    
    @ApiModelProperty(value="멤버 시퀀스", required = true)
    private int memberId;    

    @ApiModelProperty(value="데일리 시퀀스", required = true)
    private int dailyId;
    
    @ApiModelProperty(value="게시글 제목", required = true)
    private String dailyTitle;

    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private String dailyPath1;

}
