package com.foodiary.daily.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyEditRequestDto {
    
    @ApiModelProperty(value="게시글 제목", required = false)
    private String title;

    @ApiModelProperty(value="게시글 내용", required = false)
    private String content;

}
