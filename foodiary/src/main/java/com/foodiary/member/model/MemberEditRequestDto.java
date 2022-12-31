package com.foodiary.member.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
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

    @Email(message = "이메일 형식에 부합하지 않습니다.")
    @ApiModelProperty(value="사용자 이메일", required = false)
    private String email;

    @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,16}$", message = "닉네임은 2~16자리 한글, 영어 숫자만 가능합니다.")
    @ApiModelProperty(value="사용자 닉네임", required = false)
    private String nickName;

    @ApiModelProperty(value="사용자 소개글", required = false)
    private String profile;

    @ApiModelProperty(value="사용자 이미지 경로", required = false, hidden = true)
    private String memberPath;

    public void updatePath(String memberPath) {
        this.memberPath = memberPath;
    }

    public void updateId(int memberId) {
        this.memberId = memberId;
    }

}
