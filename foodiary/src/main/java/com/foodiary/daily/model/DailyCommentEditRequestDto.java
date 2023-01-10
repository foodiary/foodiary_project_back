package com.foodiary.daily.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyCommentEditRequestDto {
    
    @ApiModelProperty(value="댓글 시퀀스", required = true)

    @Setter private int commentId;

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    @Setter private int dailyId;

    @ApiModelProperty(value="회원 시퀀스", required = true)
    @Setter private int memberId;

    @ApiModelProperty(value="내용", required = true)
    private String content;

}
