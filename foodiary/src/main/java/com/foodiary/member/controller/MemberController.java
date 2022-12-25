package com.foodiary.member.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foodiary.member.dto.MemberPostDto;
import com.foodiary.member.dto.MemberSignUpDto;
import com.foodiary.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
// @RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "member sign up", description = "회원 가입하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
        @PostMapping(value = "/member/signup")
    // @PostMapping(value = "/member/signup", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> memberSignUp(
        // @RequestBody MemberSignUpDto memberSignUpDto,
        // MemberSignUpDto memberSignUpDto,
        @RequestPart MemberSignUpDto memberSignUpDto,


        // @Parameter(description="사용자 아이디", example = "dffnk555", required = true)
        // @RequestPart("loginId") String loginId,
        // @Parameter(description="사용자 비밀번호", example = "dsfnldsn!13", required = true)
        // @RequestPart("password") String password,
        // @Parameter(description="사용자 이메일", example = "sdfljkdsjnflk@naver.com", required = true)
        // @RequestPart("email") String email,
        // @Parameter(description="사용자 닉네임", example = "닉닉네임", required = true)
        // @RequestPart("nickName") String nickName,
        // @Parameter(description="사용자 소개글", example = "안녕 클레오 파트라")
        // @RequestPart(value="profile", required = false) String profile,
        @Parameter(description = "사진 이미지")
        @RequestPart(value = "memberImage", required = false) MultipartFile memberImage,
        BindingResult bindingResult, Errors errors
    ) throws Exception {

        // 멤버 정보 및 파일 받기
        // @Valid MemberSignUpDto memberSignUpDto = new MemberSignUpDto(loginId, password, email, nickName, profile, memberImage);

        // if(errors.hasFieldErrors()) {
        //     for(FieldError fieldError : errors.getFieldError()) {
        //         return new ResponseEntity<>(fieldError.getField().toString(), HttpStatus.BAD_REQUEST);
        //     }
        // }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("/addmember")
    public ResponseEntity<Void> testCreateMember() {
        // MemberPostDto PostDto = new MemberPostDto("dfjlksdjf@naver.com", "testsdnfl1234!", "닉네임", "짜장면", null, null, 'N');
        MemberPostDto postDto = new MemberPostDto("dfjlksdjf@naver.com", "testsdnfl1234!", "닉네임", "짜장면", null, null, "N");

        memberService.createdMember(postDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
