package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDailyCommentDto {
    
    @ApiModelProperty(value="멤버 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="하루 공유 댓글 시퀀스", required = true)
    private int dailyCommentId;

    @ApiModelProperty(value="하루 공유 시퀀스", required = true)
    private int dailyId;

    @ApiModelProperty(value="하루 공유 제목", required = true)
    private String dailyTitle;

    @ApiModelProperty(value="하루 공유 댓글 작성자", required = true)
    private String dailyCommentWriter;

    @ApiModelProperty(value="하루 공유 댓글 내용", required = true)
    private String dailyCommentBody;

    @ApiModelProperty(value="댓글이 없을 경우", required = true)
    private String no;
    
    public void noAdd() {
        no = "댓글이 없습니다.";
    }
}
