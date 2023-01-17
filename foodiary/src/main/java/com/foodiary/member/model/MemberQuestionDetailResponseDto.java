package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberQuestionDetailResponseDto {
    
    @ApiModelProperty(value="문의 시퀀스", required = true)
    private int questionId;

    @ApiModelProperty(value="문의 제목", required = true)
    private String questionTitle;

    @ApiModelProperty(value="문의 내용", required = true)
    private String questionContent;

    @ApiModelProperty(value="문의 이미지", required = true)
    private String questionPath;

    @ApiModelProperty(value="답변 여부", required = true)
    private String questionAnswerYn;

    @ApiModelProperty(value="문의 일자", required = true)
    private String questionCreate;

    @ApiModelProperty(value="답변 제목", required = true)
    private String answerTitle;

    @ApiModelProperty(value="답변 내용", required = true)
    private String answerContent;

    @ApiModelProperty(value="답변 일자", required = true)
    private String answerCreate;
}
