package com.foodiary.daily.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyCommentDetailsResponseDto {
 
    @ApiModelProperty(value="댓글 시퀀스", required = true)
    private int dailyCommentId;

    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int dailyId;

    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="댓글 작성자", required = true)
    private String dailyCommentWriter;


    @ApiModelProperty(value="내용", required = true)
    private String dailyCommentBody;

    @ApiModelProperty(value="댓글 생성일", required = true)
    private LocalDateTime dailyCommentCreate;

    @Setter
    @ApiModelProperty(value="본인 인증", required = true)
    private boolean userCheck;
}
