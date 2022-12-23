package com.foodiary.member.model;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberEditDto {
    
    @ApiModelProperty(value="사용자 비밀번호", required = false)
    private String password;

    @ApiModelProperty(value="사용자 이메일", required = false)
    private String email;

    @ApiModelProperty(value="사용자 닉네임", required = true)
    private String nickName;

    @ApiModelProperty(value="사용자 소개글", required = false)
    private String profile;

    @ApiModelProperty(value="사용자 이미지 경로", required = false)
    private String path;

    @ApiModelProperty(value="사용자 이미지", required = false)
    private MultipartFile memberImage;

    public MemberEditDto(String password, String email, String nickName, String profile, MultipartFile memberImage) {
        this.password = password;
        this.email = email;
        this.nickName = nickName;
        this.profile = profile;
        this.memberImage = memberImage;
    }

    public MemberEditDto(String email, String nickName, String profile, String path) {
        this.email = email;
        this.nickName = nickName;
        this.profile = profile;
        this.path = path;
    }

    

    
}
