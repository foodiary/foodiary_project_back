package com.foodiary.member.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberQuestionWriteResponseDto {

    @ApiModelProperty(value="게시글 시퀀스", required = false, hidden = true)
    private int questionId;

    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;

    @NotBlank(message = "제목이 비어있습니다.")
    @Size(max=50, message = "제목은 최대 50자입니다")
    @ApiModelProperty(value="문의 제목", required = true)
    private String questionTitle;

    @NotBlank(message = "내용이 비어있습니다.")
    @Size(max=1000, message = "제목은 최대 50자입니다")
    @ApiModelProperty(value="문의 내용", required = true)
    private String questionContent;

    @ApiModelProperty(value="문의 첨부파일", required = false)
    private String questionPath;

    public void pathUpdate(String questionPath) {
        this.questionPath = questionPath;
    }
    
}
