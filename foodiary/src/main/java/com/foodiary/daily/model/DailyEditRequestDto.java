package com.foodiary.daily.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyEditRequestDto {

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    @Setter private int dailyId;

    @ApiModelProperty(value="회원 시퀀스", required = true)
    @Setter private int memberId;

    @ApiModelProperty(value="게시글 제목", required = false)
    private String title;

    @ApiModelProperty(value="게시글 내용", required = false)
    private String content;

    @ApiModelProperty(value="삭제할 이미지 경로", required = false)
    List<String> deletePath;

    @ApiModelProperty(value="썸네일 변경 여부", required = true)
    @Setter private boolean thumbnailYn;

    @ApiModelProperty(value="변경할 썸네일 이미지 경로", required = false)
    @Setter private String thumbnailPath;
}
