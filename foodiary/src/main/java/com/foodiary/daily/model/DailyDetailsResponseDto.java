package com.foodiary.daily.model;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyDetailsResponseDto {
    
    @ApiModelProperty(value="게시글 시퀀스", required = true)
    private int dailyId;

    @ApiModelProperty(value="회원 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="게시글 제목", required = true)
    private String dailyTitle;

    @ApiModelProperty(value="게시글 내용", required = true)
    private String dailyBody;

    @ApiModelProperty(value="게시글 작성자", required = true)
    private String dailyWriter;

    @Setter
    @ApiModelProperty(value="게시글 이미지 경로", required = true)
    private List<String> dailyImageList;

    @ApiModelProperty(value="게시글 좋아요 수", required = true)
     private Integer dailyLike;

    @ApiModelProperty(value="게시글 조회 수", required = true)
    private Integer dailyView;

    @ApiModelProperty(value="작성일", required = true)
    private LocalDateTime dailyCreate;

    @ApiModelProperty(value="댓글 수", required = true)
    private int dailyComment;

    @Setter
    @ApiModelProperty(value="주간 랭킹 여부", required = true)
    private boolean weekRank;

    @Setter
    @ApiModelProperty(value="월 랭킹 여부", required = true)
    private boolean monRank;

    @Setter
    @ApiModelProperty(value="스크랩 여부 체크", required = true)
    private boolean scrapCheck;

    @Setter
    @ApiModelProperty(value="좋아요 여부 체크 인증", required = true)
    private boolean likeCheck;


    @Setter
    @ApiModelProperty(value="본인 인증", required = true)
    private boolean userCheck;

    // @ApiModelProperty(value="댓글 리스트", required = true)
    // List<DailyCommentDetailsDto> dailyCommentDtoList;
}
