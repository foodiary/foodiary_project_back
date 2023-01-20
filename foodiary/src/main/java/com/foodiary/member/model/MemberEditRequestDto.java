package com.foodiary.member.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberEditRequestDto {

    @ApiModelProperty(value="사용자 시퀀스", required = false, hidden = true)
    private int memberId;

    @NotBlank(message = "닉네임이 비어있습니다.")
    @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,10}$", message = "닉네임은 2~10자리 한글, 영어 숫자만 가능합니다.")
    @ApiModelProperty(value="사용자 닉네임", required = true)
    private String nickName;

    @ApiModelProperty(value="사용자 소개글", required = true)
    private String profile;

    public void updateId(int memberId) {
        this.memberId = memberId;
    }

}
