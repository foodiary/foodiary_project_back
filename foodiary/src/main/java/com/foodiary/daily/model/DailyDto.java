package com.foodiary.daily.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyDto {

    @ApiModelProperty(value="멤버 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int dailyId;

    @ApiModelProperty(value="게시글 제목", required = true)
    private String dailyTitle;

    @ApiModelProperty(value="게시글 작성자", required = true)
    private String dailyWriter;

    @ApiModelProperty(value="게시글 작성일자", required = true)
    private String dailyCreate;

    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private String dailyPath;

    @ApiModelProperty(value="게시글 댓글 수", required = true)
    private int dailyComment;

    @ApiModelProperty(value="게시글 조회 수", required = true)
    private int dailyView;

    @ApiModelProperty(value="게시글 좋아요 수", required = true)
    private int dailyLike;

    @ApiModelProperty(value="게시글이 없을 경우", required = true)
    private String no;
    
    public void noAdd() {
        no = "게시글이 없습니다.";
    }
}
