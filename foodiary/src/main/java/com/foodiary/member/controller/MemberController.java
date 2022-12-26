package com.foodiary.member.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foodiary.common.exception.VaildErrorDto;
import com.foodiary.common.s3.S3Service;
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

    private final S3Service s3Service;

    @Operation(summary = "member sign up", description = "회원 가입하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/member/signup")
    public ResponseEntity<?> memberSignUp(
        @RequestPart @Valid MemberSignUpDto memberSignUpDto,
        @Parameter(description = "사진 이미지")
        @RequestPart(value = "memberImage", required = false) MultipartFile memberImage
    ) throws Exception {

        if(memberSignUpDto.getMore_password().equals(memberSignUpDto.getPassword())==false) {
            
            VaildErrorDto vaildErrorDto = new VaildErrorDto("more_password", "비밀번호가 일치하지 않습니다", 400);

            return new ResponseEntity<>(vaildErrorDto, HttpStatus.BAD_REQUEST);
        }

        if(memberImage!=null) {
            // System.out.println("체크 하기 : "+ System.getProperty("user.dir"));
            s3Service.upload(memberImage, "member");
        } 

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
