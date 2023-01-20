package com.foodiary.daily.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyCommentEditRequestDto {


    @ApiModelProperty(value="댓글 시퀀스", required = true)
    @Setter private int commentId;

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    @Setter private int dailyId;

    @NotBlank(message = "회원 시퀀스를 입력해주세요.")
    @ApiModelProperty(value="회원 시퀀스", required = true)
    @Setter private int memberId;


    @NotBlank(message = "내용이 비어있습니다.")
    @ApiModelProperty(value="내용", required = true)
    private String content;

}
