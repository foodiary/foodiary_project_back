package com.foodiary.daily.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyWriteRequestDto {

    @Setter
    private int dailyId;

    @NotNull
    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;
    
    @NotBlank(message = "제목이 비어있습니다.")
    @ApiModelProperty(value="게시글 제목", required = true)
    private String title;

    @NotBlank(message = "내용이 비어있습니다.")
    @ApiModelProperty(value="게시글 내용", required = true)
    private String content;

    @ApiModelProperty(value="게시글 작성자", required = false)
    @Setter private String writer;

    @ApiModelProperty(value="이미지 경로1", required = false)
    @Setter private String path1;

    @ApiModelProperty(value="이미지 경로2", required = false)
    @Setter private String path2;

    @ApiModelProperty(value="이미지 경로3", required = false)
    @Setter private String path3;

    @ApiModelProperty(value="이미지 경로4", required = false)
    @Setter private String path4;

    @ApiModelProperty(value="이미지 경로5", required = false)
    @Setter private String path5;
}
