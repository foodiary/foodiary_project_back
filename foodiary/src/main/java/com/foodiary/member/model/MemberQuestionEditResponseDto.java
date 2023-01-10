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
    
    @NotBlank(message = "제목이 비어있습니다.")
    @ApiModelProperty(value="문의 제목", required = true)
    private String questionTitle;

    @NotBlank(message = "내용이 비어있습니다.")
    @ApiModelProperty(value="문의 내용", required = true)
    private String questionContent;

    @ApiModelProperty(value="문의 파일 삭제 여부", required = true)
    private String imageDelete;

    @ApiModelProperty(value="문의 첨부파일", required = false, hidden = true)
    private String questionPath;

    public void pathUpadte(String path) {
        questionPath = path;
    }

}
