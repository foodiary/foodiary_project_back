package com.foodiary.member.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpDto {

    @ApiModelProperty(value="사용자 시퀀스", required = false, hidden = true)
    private int memberId;

    @NotBlank(message = "아이디가 비어있습니다")
    @Pattern(regexp = "^[a-z0-9]{6,13}$", message = "아이디는 6~13자리 영문소문자, 숫자만 가능합니다")
    @ApiModelProperty(value="사용자 아이디", required = true)
    private String loginId;

    @NotBlank(message = "비밀번호가 비어있습니다")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,16}$", message = "비밀번호는 8~16자리 영문자, 숫자, 특수문자를 포함해야합니다.")
    @ApiModelProperty(value="사용자 비밀번호", required = true)
    private String password;

    @NotBlank(message = "비밀번호를 한번 더 입력하세요")
    private String more_password;

    @NotBlank(message = "이메일이 비어있습니다")
    @Email(message = "이메일 형식에 부합하지 않습니다.")
    @ApiModelProperty(value="사용자 이메일", required = true)
    private String email;

    @NotBlank(message = "닉네임이 비어있습니다.")
    @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,16}$", message = "닉네임은 2~16자리 한글, 영어 숫자만 가능합니다.")
    @ApiModelProperty(value="사용자 닉네임", required = true)
    private String nickName;

    @ApiModelProperty(value="사용자 소개글", required = false)
    private String profile;

    // @ApiModelProperty(value="사용자 파일", required = false)
    // private MultipartFile memberImage;

    public MemberSignUpDto(
            @NotBlank(message = "아이디가 비어있습니다") @Pattern(regexp = "^[a-z0-9]{5,20}$", message = "아이디는 6~13자리 영문소문자, 숫자만 가능합니다") String loginId,
            @NotBlank(message = "비밀번호가 비어있습니다") @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,16}$", message = "비밀번호는 8~16자리 영문자, 숫자, 특수문자를 포함해야합니다.") String password,
            @NotBlank(message = "비밀번호를 한번 더 입력하세요") String more_password,
            @NotBlank(message = "이메일이 비어있습니다") @Email(message = "이메일 형식에 부합하지 않습니다.") String email,
            @NotBlank(message = "닉네임이 비어있습니다.") @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,16}$", message = "닉네임은 2~16자리 한글, 영어 숫자만 가능합니다.") String nickName,
            String profile) {
        this.loginId = loginId;
        this.password = password;
        this.more_password = more_password;
        this.email = email;
        this.nickName = nickName;
        this.profile = profile;
        // this.memberImage = memberImage;
    }

    // public MemberSignUpDto(
    //         @NotBlank(message = "아이디가 비어있습니다") @Pattern(regexp = "^[a-z0-9]{5,20}$", message = "아이디는 6~13자리 영문소문자, 숫자만 가능합니다") String loginId,
    //         @NotBlank(message = "비밀번호가 비어있습니다") @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,16}$", message = "비밀번호는 8~16자리 대문자, 소문자, 특수문자를 포함해야합니다.") String password,
    //         @NotBlank(message = "비밀번호를 한번 더 입력하세요") String more_password,
    //         @NotBlank(message = "이메일이 비어있습니다") @Email(message = "이메일 형식에 부합하지 않습니다.") String email,
    //         @NotBlank(message = "닉네임이 비어있습니다.") @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,16}$", message = "닉네임은 2~16자리 한글, 영어 숫자만 가능합니다.") String nickName,
    //         String profile) {
    //     this.loginId = loginId;
    //     this.password = password;
    //     this.more_password = more_password;
    //     this.email = email;
    //     this.nickName = nickName;
    //     this.profile = profile;
    // }

    
}