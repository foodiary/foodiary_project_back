package com.foodiary.member.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberOtherMemberResponseDto {
    
    @ApiModelProperty(value="멤버 시퀀스", required = true)
    private int memberId;

    @ApiModelProperty(value="회원 정보", required = true)
    MemberProfileResponseDto memberProfileResponseDtos;
    
    @ApiModelProperty(value="회원 게시글", required = true)
    List<MemberDailyResponseDto> memberDailyResponseDtos;
}
