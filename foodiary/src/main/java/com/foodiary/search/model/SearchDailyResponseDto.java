package com.foodiary.search.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchDailyResponseDto {
    
    @ApiModelProperty(value="데일리 시퀀스", required = true)
    private int dailyId;
    
    @ApiModelProperty(value="게시글 제목", required = true)
    private String dailyTitle;

    @ApiModelProperty(value="게시글 직상지", required = true)
    private String dailyWriter;

    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private String dailyPath1;

    @ApiModelProperty(value="게시글 댓글 수", required = true)
    private int dailyComment;

    @ApiModelProperty(value="게시글 조회 수", required = true)
    private int dailyView;

    @ApiModelProperty(value="게시글 좋아요 수", required = true)
    private int dailyLike;

}
