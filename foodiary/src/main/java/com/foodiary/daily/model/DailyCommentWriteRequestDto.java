package com.foodiary.daily.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyCommentWriteRequestDto {

    @NotNull
    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int dailyId;

    @NotNull
    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;

    @Setter
    @ApiModelProperty(value="댓글 작성자", required = true)
    private String writer;

    @NotBlank(message = "내용이 비어있습니다.")
    @ApiModelProperty(value="내용", required = true)
    private String content;

}
