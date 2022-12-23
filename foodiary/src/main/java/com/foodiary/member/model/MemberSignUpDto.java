package com.foodiary.member.model;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpDto {

    @ApiModelProperty(value="사용자 시퀀스", required = false, hidden = true)
    private int id;

    @ApiModelProperty(value="사용자 아이디", required = true)
    private String loginId;

    @ApiModelProperty(value="사용자 비밀번호", required = true)
    private String password;

    @ApiModelProperty(value="사용자 이메일", required = true)
    private String email;

    @ApiModelProperty(value="사용자 닉네임", required = true)
    private String nickName;

    @ApiModelProperty(value="사용자 소개글", required = false)
    private String profile;

    @ApiModelProperty(value="사용자 이미지", required = false)
    private MultipartFile memberImage;
    
}
