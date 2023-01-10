package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberNoticeResponseDto {
    
    @ApiModelProperty(value="공지 시퀀스", required = true)
    private int noticeId;

    @ApiModelProperty(value="공지 제목", required = true)
    private String noticeTitle;

    @ApiModelProperty(value="공지 일자", required = true)
    private String noticeCreate;

}
