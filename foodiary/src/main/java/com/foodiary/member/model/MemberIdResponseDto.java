package com.foodiary.member.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberIdResponseDto {
    
    @ApiModelProperty(value="사용자 시퀀스", required = true)
    private int memberId;
    
}
