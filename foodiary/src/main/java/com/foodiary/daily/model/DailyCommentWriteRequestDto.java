package com.foodiary.daily.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyCommentWriteRequestDto {

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int dailyId;

    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;

    @Setter
    @ApiModelProperty(value="댓글 작성자", required = true)
    private String writer;

    @ApiModelProperty(value="내용", required = true)
    private String content;

}
