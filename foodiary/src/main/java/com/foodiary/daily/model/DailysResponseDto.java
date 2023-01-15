package com.foodiary.daily.model;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailysResponseDto {

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int dailyId;

    @ApiModelProperty(value="게시글 제목", required = true)
    private String dailyTitle;

    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private String dailyPath;

    @ApiModelProperty(value="게시글 좋아요 수", required = true)
    private int dailyLike;

    @ApiModelProperty(value="게시글 조회 수", required = true)
    private int dailyView;

    @ApiModelProperty(value="작성일", required = true)
    private LocalDateTime dailyCreate;

    @ApiModelProperty(value="댓글 수", required = true)
    private int dailyComment;

}
