package com.foodiary.member.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCommentResponseDto {
    
    @ApiModelProperty(value="사용자 하루 식단 댓글")
    private List<MemberDailyCommentDto> memberDailyScrapResponseDtoList;

    @ApiModelProperty(value="사용자 레시피 공유 댓글")
    private List<MemberRecipeCommentDto> memberRecipeScrapResponseDtoList;
}
