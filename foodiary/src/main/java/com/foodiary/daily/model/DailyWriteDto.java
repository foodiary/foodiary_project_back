package com.foodiary.daily.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyWriteDto {
    
    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;
    
    @ApiModelProperty(value="게시글 제목", required = true)
    private String title;

    @ApiModelProperty(value="게시글 내용", required = true)
    private String content;

    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private String path;

}
