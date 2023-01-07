package com.foodiary.member.model;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCommentRequestDto {
    
    @NotBlank(message = "댓글 내용이 비어있습니다")
    @ApiModelProperty(value="댓글 내용", required = true)
    private String comment;
}
