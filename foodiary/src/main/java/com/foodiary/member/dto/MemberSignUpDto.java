package com.foodiary.member.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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

    @NotBlank(message = "id_chk1")
    @Pattern(regexp = "^[a-z0-9]{5,20}$", message = "id_chk2")
    @ApiModelProperty(value="사용자 아이디", required = true)
    private String loginId;

    @NotBlank(message = "pw_chk1")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,16}$", message = "pw_chk2")
    @ApiModelProperty(value="사용자 비밀번호", required = true)
    private String password;

    @NotBlank(message = "pw_more_chk1")
    private String more_password;

    @NotBlank(message = "email_chk1")
    @Email(message = "email_chk2")
    @ApiModelProperty(value="사용자 이메일", required = true)
    private String email;

    @NotBlank(message = "nick_chk1")
    @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,16}$", message = "nick_chk2")
    @ApiModelProperty(value="사용자 닉네임", required = true)
    private String nickName;

    @ApiModelProperty(value="사용자 소개글", required = false)
    private String profile;

    // @ApiModelProperty(value="사용자 이미지", required = false)
    // private MultipartFile memberImage;

    public MemberSignUpDto(String loginId, String password, String email, String nickName, String profile
    , MultipartFile memberImage
            ) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.nickName = nickName;
        this.profile = profile;
        // this.memberImage = memberImage;
    }
    
    
}
