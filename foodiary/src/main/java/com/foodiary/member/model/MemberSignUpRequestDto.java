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
public class MemberSignUpRequestDto {

    @ApiModelProperty(value="사용자 시퀀스", required = false, hidden = true)
    private int memberId;

    @NotBlank(message = "아이디가 비어있습니다")
    @Pattern(regexp = "^[a-z0-9]{6,13}$", message = "아이디는 6~13자리 영문소문자, 숫자만 가능합니다")
    @ApiModelProperty(value="사용자 아이디", required = true)
    private String loginId;

    @ApiModelProperty(value="사용자 아이디 검사, Y를 보내주지 않으면 무조건 에러 처리", required = true)
    @NotBlank(message = "아이디 검사를 하지 않았습니다.")
    @Pattern(regexp = "^[Y]$", message = "잘못된 요청입니다.")
    private String loginYn;

    @NotBlank(message = "비밀번호가 비어있습니다")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,16}$", message = "비밀번호는 8~16자리 영문자, 숫자, 특수문자를 포함해야합니다.")
    @ApiModelProperty(value="사용자 비밀번호", required = true)
    private String password;

    @NotBlank(message = "비밀번호를 한번 더 입력하세요")
    @ApiModelProperty(value="사용자 비밀번호 확인", required = true)
    private String more_password;

    @ApiModelProperty(value="사용자 비밀번호 검사, Y를 보내주지 않으면 무조건 에러 처리", required = true)
    @NotBlank(message = "비밀번호 검사를 하지 않았습니다.")
    @Pattern(regexp = "^[Y]$", message = "잘못된 요청입니다.")
    private String passwordYn;

    @NotBlank(message = "이메일이 비어있습니다")
    @Email(message = "이메일 형식에 부합하지 않습니다.")
    @ApiModelProperty(value="사용자 이메일", required = true)
    private String email;

    @ApiModelProperty(value="사용자 이메일 검사, Y를 보내주지 않으면 무조건 에러 처리", required = true)
    @NotBlank(message = "이메일 검사를 하지 않았습니다.")
    @Pattern(regexp = "^[Y]$", message = "잘못된 요청입니다.")
    private String emailYn;

    @NotBlank(message = "닉네임이 비어있습니다.")
    @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,16}$", message = "닉네임은 2~16자리 한글, 영어 숫자만 가능합니다.")
    @ApiModelProperty(value="사용자 닉네임", required = true)
    private String nickName;

    @ApiModelProperty(value="사용자 닉네임 검사, Y를 보내주지 않으면 무조건 에러 처리", required = true)
    @NotBlank(message = "닉네임 검사를 하지 않았습니다.")
    @Pattern(regexp = "^[Y]$", message = "잘못된 요청입니다.")
    private String nickNameYn;

    @ApiModelProperty(value="사용자 이미지 경로", required = false, hidden = true)
    private String memberPath;

    @ApiModelProperty(value="사용자 소개글", required = false)
    private String profile;

    @NotBlank(message = "필수약관 동의가 비어있습니다.")
    @ApiModelProperty(value="필수약관 동의", required = true)
    private String requiredTerms;

    @NotBlank(message = "선택약관 동의가 비어있습니다.")
    @ApiModelProperty(value="선택약관 동의", required = true)
    private String choiceTerms;

    public MemberSignUpRequestDto(
            @NotBlank(message = "아이디가 비어있습니다") @Pattern(regexp = "^[a-z0-9]{6,13}$", message = "아이디는 6~13자리 영문소문자, 숫자만 가능합니다") String loginId,
            @NotBlank(message = "아이디 검사를 하지 않았습니다.") @Pattern(regexp = "^[Y]$", message = "잘못된 요청입니다.") String loginYn,
            @NotBlank(message = "비밀번호가 비어있습니다") @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,16}$", message = "비밀번호는 8~16자리 영문자, 숫자, 특수문자를 포함해야합니다.") String password,
            @NotBlank(message = "비밀번호를 한번 더 입력하세요") String more_password,
            @NotBlank(message = "비밀번호 검사를 하지 않았습니다.") @Pattern(regexp = "^[Y]$", message = "잘못된 요청입니다.") String passwordYn,
            @NotBlank(message = "이메일이 비어있습니다") @Email(message = "이메일 형식에 부합하지 않습니다.") String email,
            @NotBlank(message = "이메일 검사를 하지 않았습니다.") @Pattern(regexp = "^[Y]$", message = "잘못된 요청입니다.") String emailYn,
            @NotBlank(message = "닉네임이 비어있습니다.") @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,16}$", message = "닉네임은 2~16자리 한글, 영어 숫자만 가능합니다.") String nickName,
            @NotBlank(message = "닉네임 검사를 하지 않았습니다.") @Pattern(regexp = "^[Y]$", message = "잘못된 요청입니다.") String nickNameYn,
            String profile, @NotBlank(message = "필수약관 동의가 비어있습니다.") String requiredTerms,
            @NotBlank(message = "선택약관 동의가 비어있습니다.") String choiceTerms) {
        this.loginId = loginId;
        this.loginYn = loginYn;
        this.password = password;
        this.more_password = more_password;
        this.passwordYn = passwordYn;
        this.email = email;
        this.emailYn = emailYn;
        this.nickName = nickName;
        this.nickNameYn = nickNameYn;
        this.profile = profile;
        this.requiredTerms = requiredTerms;
        this.choiceTerms = choiceTerms;
    }

    public void passwordUpdate(String newPassword) {
        this.password = newPassword;
    }

    public void pathUpdate(String memberPath) {
        this.memberPath = memberPath;
    }

}