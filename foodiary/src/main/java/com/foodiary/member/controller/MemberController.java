package com.foodiary.member.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.foodiary.daily.model.DailyDto;
import com.foodiary.main.model.FoodDto;
import com.foodiary.main.model.FoodRecommendDto;
import com.foodiary.member.model.MemberDetailsDto;
import com.foodiary.member.model.MemberLoginDto;
import com.foodiary.member.model.MemberScrapDto;
import com.foodiary.member.model.MemberSerchDto;
import com.foodiary.recipe.model.RecipeDto;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
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

    @Operation(summary = "member info modify", description = "회원 정보 수정")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @PatchMapping(value = "/member/{memberId}")
    // public ResponseEntity<MemberEditDto> memberEdit(
    public ResponseEntity<String> memberModify(
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

        // MemberEditDto memberEditDto = new MemberEditDto(email, nickName, profile, "이미지 경로");
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member info", description = "회원 정보 보기(본인꺼) 마이페이지")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member")
    public ResponseEntity<MemberDetailsDto> memberDetails(
        @ApiParam(value = "회원 시퀀스", required = true)int memberId
    ) throws Exception {

        MemberDetailsDto memberDetails = new MemberDetailsDto("사용자 아이디", "사용자 이메일", "사용자 닉네임", "사용자 소개글", "사용자 이미지 경로");

        return new ResponseEntity<>(memberDetails, HttpStatus.OK);
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
        @RequestBody MemberLoginDto memberLoginDto
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
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
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
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @DeleteMapping(value = "/member/{memberId}")
    public ResponseEntity<String> memberDelete(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId
    ) throws Exception {

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member search", description = "회원 정보 조회(다른 사람 및 본인 프로필 조회)")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/member/search")
    public ResponseEntity<MemberSerchDto> memberDetailOther(
        @ApiParam(value = "회원 시퀀스", required = true)int memberId
    ) throws Exception {

        List<DailyDto> dailyList = new ArrayList<>();

        List<RecipeDto> recipeList = new ArrayList<>();

        MemberSerchDto memberSerchDto = new MemberSerchDto(dailyList, recipeList, "사용자 소개글 입니다.", "이미지 경로", 5);
        
        return new ResponseEntity<>(memberSerchDto, HttpStatus.OK);
    }

    // 회원 전체 조회
    @ApiOperation(value = "사용자 전체 조회", hidden = true)
    @GetMapping(value = "/member/search/all")
    public void members() throws Exception {
        
    }

    @Operation(summary = "member reissue", description = "회원 토큰 재발급")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @PostMapping(value = "/member/reissue")
    public ResponseEntity<String> memberReissue() throws Exception {

        // 토큰 재발급해서 저장하고 토큰 반환
        String token = "토큰입니다";
        
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @Operation(summary = "member scrap list", description = "회원(본인) 스크랩 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member/scrap")
    public ResponseEntity<MemberScrapDto> scraps(
        @ApiParam(value = "memberId", required = true) int memberId
    ) throws Exception {

        List<DailyDto> dailyList = new ArrayList<>();

        List<RecipeDto> recipeList = new ArrayList<>();
        
        MemberScrapDto memberScrap = new MemberScrapDto(dailyList, recipeList);
        
        return new ResponseEntity<>(memberScrap, HttpStatus.OK);
    }

    @Operation(summary = "member daily scrap delete", description = "회원 하루식단 스크랩 삭제")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @DeleteMapping(value = "/member/scrap/daily/{scrapId}/{memberId}")
    public ResponseEntity<String> scrapDailyDelete(
        @PathVariable @ApiParam(value = "스크랩 시퀀스", required = true) int scrapId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId
    ) throws Exception {

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member recipe scrap delete", description = "회원 레시피 스크랩 삭제")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @DeleteMapping(value = "/member/scrap/recipe/{scrapId}/{memberId}")
    public ResponseEntity<String> scrapRecipeDelete(
        @PathVariable @ApiParam(value = "스크랩 시퀀스", required = true) int scrapId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId
    ) throws Exception {

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member food recommend", description = "회원 음식 추천 목록")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member/food/{memberId}")
    public ResponseEntity<List<FoodDto>> memberFood(
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
    ) throws Exception {

        FoodDto foodDto = new FoodDto("짬뽕", "중식", LocalDateTime.now());

        List<FoodDto> foodList = new ArrayList<>();

        foodList.add(foodDto);

        return new ResponseEntity<>(foodList, HttpStatus.OK);
    }

    @Operation(summary = "member food List", description = "회원 음식 식단 리스트")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member/food/list/{memberId}")
    public ResponseEntity<List<FoodRecommendDto>> memberFoodList(
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
    ) throws Exception {

        FoodRecommendDto foodRecommendDto = new FoodRecommendDto(null, null, null, null,
        null, null, null, null, 
        null, null, null, null, 
        null, null, null, null, 
        null, null, null, null, 
        null, null, null, null, 
        null, null, null, null);

        List<FoodRecommendDto> foodList = new ArrayList<>();

        foodList.add(foodRecommendDto);

        return new ResponseEntity<>(foodList, HttpStatus.OK);
    }


    

    



}
