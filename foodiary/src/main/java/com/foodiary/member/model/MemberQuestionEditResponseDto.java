package com.foodiary.member.model;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberQuestionEditResponseDto {
    
    @ApiModelProperty(value="멤버 시퀀스", required = false)
    private int memberId;    

    @ApiModelProperty(value="게시글 시퀀스", required = false)
    private int questionId;    

    @NotBlank(message = "제목이 비어있습니다.")
    @ApiModelProperty(value="문의 제목", required = true)
    private String questionTitle;

    @NotBlank(message = "내용이 비어있습니다.")
    @ApiModelProperty(value="문의 내용", required = true)
    private String questionContent;

    @ApiModelProperty(value="기존 첨부파일 경로", required = false)
    private String questionPath;

    public void pathUpadte(String path) {
        questionPath = path;
    }

    public void memberIdUpadte(int memberId) {
        this.memberId = memberId;
    }

    public void questionIdUpadte(int questionId) {
        this.questionId = questionId;
    }

}
