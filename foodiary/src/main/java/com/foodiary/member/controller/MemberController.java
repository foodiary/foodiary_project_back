package com.foodiary.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.foodiary.member.model.MemberEditDto;
import com.foodiary.member.model.MemberLoginDto;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Controller
public class MemberController {
    
    @Operation(summary = "member sign up", description = "회원 가입하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    // @PostMapping(value = "/member/signup")
    // @PostMapping(value = "/member/signup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    // @PostMapping(value = "/member/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PostMapping(value = "/member/signup")
    public ResponseEntity<String> memberSignUp(
        // @RequestBody MemberSignUpDto memberSignUpDto,
        // @RequestPart MemberSignUpDto memberSignUpDto
        // @RequestPart MemberSignUpDto memberSignUpDto,
        @Parameter(description="사용자 아이디", example = "dffnk555", required = true)
        @RequestPart("loginId") String loginId,
        @Parameter(description="사용자 비밀번호", example = "dsfnldsn!13", required = true)
        @RequestPart("password") String password,
        @Parameter(description="사용자 이메일", example = "sdfljkdsjnflk@naver.com", required = true)
        @RequestPart("email") String email,
        @Parameter(description="사용자 닉네임", example = "닉닉네임", required = true)
        @RequestPart("nickName") String nickName,
        @Parameter(description="사용자 소개글", example = "안녕 클레오 파트라")
        @RequestPart(value="profile", required = false) String profile,
        @Parameter(description = "사진 이미지")
        @RequestPart(value = "memberImage", required = false) MultipartFile memberImage
    ) throws Exception {

        // 멤버 정보 및 파일 받기

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member info edit", description = "회원 정보 수정")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    // @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    // @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @PatchMapping(value = "/member/{memberId}")
    public ResponseEntity<MemberEditDto> memberEdit(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId, 
        @Parameter(description="사용자 비밀번호", example = "dsfnldsn!13")
        @RequestPart(value = "password", required = false) String password,
        @Parameter(description="사용자 이메일", example = "sdfljkdsjnflk@naver.com")
        @RequestPart(value = "email", required = false) String email,
        @Parameter(description="사용자 닉네임", example = "닉닉네임")
        @RequestPart(value = "nickName", required = false) String nickName,
        @Parameter(description="사용자 소개글", example = "안녕 클레오 파트라")
        @RequestPart(value="profile", required = false) String profile,
        @Parameter(description = "사진 이미지")
        @RequestPart(value = "memberImage", required = false) MultipartFile memberImage
    ) throws Exception {

        MemberEditDto memberEditDto = new MemberEditDto(email, nickName, profile, "이미지 경로");
        // 멤버 정보 및 파일 받기
        return new ResponseEntity<>(memberEditDto, HttpStatus.OK);
    }

    @Operation(summary = "member info", description = "회원 정보 보기(본인꺼)")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/member/{memberId}")
    public ResponseEntity<MemberEditDto> memberView(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId
    ) throws Exception {

        MemberEditDto memberEditDto = new MemberEditDto("dflkds@naver.com", "닉닉네임", "자기 소개하기", "파일경로다");

        return new ResponseEntity<>(memberEditDto, HttpStatus.OK);
    }
    
    @Operation(summary = "member login", description = "로그인")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/member/login")
    public ResponseEntity<String> memberLogin(
        MemberLoginDto memberLoginDto
    ) throws Exception {

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member logout", description = "로그아웃")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/member/logout")
    public ResponseEntity<String> memberLogout(
    ) throws Exception {

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member delete", description = "회원 탈퇴")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @DeleteMapping(value = "/member/{memberId}")
    public ResponseEntity<String> memberDelete(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId
    ) throws Exception {

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    // TODO : 진행중, 미완성
    @Operation(summary = "member search", description = "회원 정보 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @DeleteMapping(value = "/member/search/{memberId}")
    public ResponseEntity<String> memberSearch(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId
    ) throws Exception {
        // 그사람이 쓴 게시글, 프로필 이미지, 프로필 내용, 좋아요 받은수
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
