package com.foodiary.member.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import com.foodiary.common.exception.VaildErrorDto;
import com.foodiary.common.s3.S3Service;
import com.foodiary.daily.model.DailysResponseDto;
import com.foodiary.main.model.FoodDtooo;
import com.foodiary.main.model.FoodRecommendDto;
import com.foodiary.member.model.MemberDetailsDto;
import com.foodiary.member.model.MemberEditDto;
import com.foodiary.member.model.MemberLoginDto;
import com.foodiary.member.model.MemberScrapDto;
import com.foodiary.member.model.MemberSerchDto;
import com.foodiary.member.model.MemberSignUpDto;
import com.foodiary.member.service.MemberService;
import com.foodiary.recipe.model.RecipesResponseDto;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
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

        memberService.createdMember(memberSignUpDto);
        if(memberImage!=null) {
            System.out.println("체크 하기 : "+ System.getProperty("user.dir"));
            // s3Service.upload(memberImage, "member");
        } 

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
    public ResponseEntity<String> memberModify(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId,
        @RequestPart @Valid MemberEditDto memberEditDto,
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
        @ApiParam(value = "회원 시퀀스", required = true)int memberId,
        HttpServletRequest request
    ) throws Exception {

        // System.out.println("값 찍기 : "+request.getHeader("accessToken"));
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

        DailysResponseDto dailysResponseDto = new DailysResponseDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);

        List<DailysResponseDto> dailyList = new ArrayList<>();

        dailyList.add(dailysResponseDto);

        RecipesResponseDto recipesResponseDto = new RecipesResponseDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);

        List<RecipesResponseDto> recipeList = new ArrayList<>();

        recipeList.add(recipesResponseDto);

        MemberSerchDto memberSerchDto = new MemberSerchDto(dailyList, recipeList, "사용자 소개글 입니다.", "이미지 경로", 5);
        
        return new ResponseEntity<>(memberSerchDto, HttpStatus.OK);
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

        DailysResponseDto dailysResponseDto = new DailysResponseDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);

        List<DailysResponseDto> dailyList = new ArrayList<>();

        dailyList.add(dailysResponseDto);

        RecipesResponseDto recipesResponseDto = new RecipesResponseDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);

        List<RecipesResponseDto> recipeList = new ArrayList<>();

        recipeList.add(recipesResponseDto);

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
    public ResponseEntity<List<FoodDtooo>> memberFood(
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
    ) throws Exception {

        FoodDtooo foodDto = new FoodDtooo("짬뽕", "중식", LocalDateTime.now());

        List<FoodDtooo> foodList = new ArrayList<>();

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

        FoodRecommendDto foodRecommendDto = new FoodRecommendDto("한식", "된장찌개", "중식", "짬뽕",
        "한식", "된장찌개", "중식", "짬뽕",
        "한식", "된장찌개", "중식", "짬뽕",
        "한식", "된장찌개", "중식", "짬뽕",
        "한식", "된장찌개", "중식", "짬뽕",
        "한식", "된장찌개", "중식", "짬뽕",
        "한식", "된장찌개", "중식", "짬뽕");

        List<FoodRecommendDto> foodList = new ArrayList<>();

        foodList.add(foodRecommendDto);

        return new ResponseEntity<>(foodList, HttpStatus.OK);
    }


    

    



}
