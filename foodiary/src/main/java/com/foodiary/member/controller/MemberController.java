package com.foodiary.member.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.foodiary.auth.service.UserService;
import com.foodiary.common.email.EmailService;
import com.foodiary.common.exception.VaildErrorResponseDto;
import com.foodiary.common.s3.S3Service;
import com.foodiary.daily.model.DailysDto;
import com.foodiary.member.model.MemberCheckEmailRequestDto;
import com.foodiary.member.model.MemberCheckIdRequestDto;
import com.foodiary.member.model.MemberCheckNicknameRequestDto;
import com.foodiary.member.model.MemberDetailsResponseDto;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberEditPasswordRequestDto;
import com.foodiary.member.model.MemberEditRequestDto;
import com.foodiary.member.model.MemberImageDto;
import com.foodiary.member.model.MemberLoginRequestDto;
import com.foodiary.member.model.MemberScrapResponseDto;
import com.foodiary.member.model.MemberSerchResponseDto;
import com.foodiary.member.model.MemberSignUpRequestDto;
import com.foodiary.member.service.MemberService;
import com.foodiary.recipe.model.RecipesDto;

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

    private final UserService userService;

    private final EmailService emailService;

    private final S3Service s3Service;

    @GetMapping("/email/test")
    @ResponseBody
    public String emailTest() throws IOException{
        emailService.EmailSend();
        return "OK";
    }

    @Operation(summary = "member password edit", description = "비밀번호 수정하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PatchMapping(value = "/member/password/{memberId}")
    public ResponseEntity<?> memberModifyPassword(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId,
        @RequestBody MemberEditPasswordRequestDto memberEditPasswordRequestDto
    ) throws Exception {

        memberService.EditMemberPassWord(memberEditPasswordRequestDto.getPassword(), memberId);

        // TODO: 메일 발송 로직 추가, 메일에 비밀번호를 변경할수 있는 링크를 보내야함

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member password Find", description = "비밀번호 찾기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/member/find/password")
    public ResponseEntity<?> memberFindPassword(
        @RequestBody MemberCheckEmailRequestDto memberCheckEmailRequestDto
    ) throws Exception {

        MemberDto member = memberService.findmemberEmail(memberCheckEmailRequestDto.getEmail());

        if(member==null) {
            return new ResponseEntity<>("해당 회원이 존재하지 않습니다", HttpStatus.OK);
        }
        // TODO: 메일 발송 로직 추가, 메일에 비밀번호를 변경할수 있는 링크를 보내야함


        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member id Find", description = "아이디 찾기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/member/find/id")
    public ResponseEntity<?> memberFindId(
        @RequestBody MemberCheckEmailRequestDto memberCheckEmailRequestDto
    ) throws Exception {

        MemberDto member = memberService.findmemberEmail(memberCheckEmailRequestDto.getEmail());

        if(member==null) {
            return new ResponseEntity<>("해당 회원이 존재하지 않습니다", HttpStatus.OK);
        }
        // TODO: 메일 발송 로직 추가, id 정보를 메일로 발송

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    @Operation(summary = "member id check", description = "아이디 중복 검사")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/member/check/loginid")
    public ResponseEntity<?> memberCheckLoginId(
        // @RequestBody Map<String, String> idMap
        @RequestBody MemberCheckIdRequestDto memberCheckIdRequestDto
    ) throws Exception {

        if(memberCheckIdRequestDto.getLoginId()==null) {
            return new ResponseEntity<>("아이디를 입력해주세요", HttpStatus.BAD_REQUEST);
        } 
        else if(memberCheckIdRequestDto.getLoginId().isEmpty()) {
            return new ResponseEntity<>("아이디를 입력해주세요", HttpStatus.BAD_REQUEST);
        }

        MemberDto memberDto = memberService.findMemberLoginId(memberCheckIdRequestDto.getLoginId());

        if(memberDto==null) {
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        return new ResponseEntity<>("아이디가 중복입니다", HttpStatus.OK);
    }

    @Operation(summary = "member nickname check", description = "닉네임 중복 검사")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/member/check/nickname")
    public ResponseEntity<?> memberCheckNickname(
        @RequestBody MemberCheckNicknameRequestDto memberCheckNicknameRequestDto
    ) throws Exception {

        if(memberCheckNicknameRequestDto.getNickName()==null) {
            return new ResponseEntity<>("닉네임을 입력해주세요", HttpStatus.BAD_REQUEST);
        } 
        else if(memberCheckNicknameRequestDto.getNickName().isEmpty()) {
            return new ResponseEntity<>("닉네임을 입력해주세요", HttpStatus.BAD_REQUEST);

        }

        MemberDto memberDto = memberService.findmemberNickname(memberCheckNicknameRequestDto.getNickName());

        if(memberDto==null) {
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        return new ResponseEntity<>("닉네임이 중복입니다", HttpStatus.OK);
    }

    @Operation(summary = "member email check", description = "이메일 중복 검사")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/member/check/email")
    public ResponseEntity<?> memberCheckEmail(
        @RequestBody MemberCheckEmailRequestDto memberCheckEmailRequestDto
    ) throws Exception {

        if(memberCheckEmailRequestDto.getEmail()==null) {
            return new ResponseEntity<>("이메일을 입력해주세요", HttpStatus.BAD_REQUEST);
        } 
        else if(memberCheckEmailRequestDto.getEmail().isEmpty()) {
            return new ResponseEntity<>("이메일을 입력해주세요", HttpStatus.BAD_REQUEST);
        }

        MemberDto memberDto = memberService.findmemberEmail(memberCheckEmailRequestDto.getEmail());

        if(memberDto==null) {
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }
        return new ResponseEntity<>("이메일이 중복입니다", HttpStatus.OK);
    }

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
        @RequestPart @Valid MemberSignUpRequestDto memberSignUpDto,
        @Parameter(description = "사진 이미지")
        @RequestPart(value = "memberImage", required = false) MultipartFile memberImage
    ) throws Exception {

        if(memberSignUpDto.getMore_password().equals(memberSignUpDto.getPassword())==false) {
            
            VaildErrorResponseDto vaildErrorDto = new VaildErrorResponseDto("more_password", "비밀번호가 일치하지 않습니다", 400);

            return new ResponseEntity<>(vaildErrorDto, HttpStatus.BAD_REQUEST);
        }

        String newPassword = userService.encrypt(memberSignUpDto.getPassword());

        memberSignUpDto.passwordUpdate(newPassword);

        memberService.createdMember(memberSignUpDto);

        if(memberImage!=null) {

        MemberDto memberDto = memberService.findMemberLoginId(memberSignUpDto.getLoginId());
        // memberService.
        String fileFullName = memberImage.getOriginalFilename();
        String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
        System.out.println("파일 이름 : "+fileName);
        String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
        System.out.println("확장자 : "+ext);

            // System.out.println("체크 하기 : "+ System.getProperty("user.dir"));
            HashMap<String, String> fileMap = s3Service.upload(memberImage, "member");
            
            MemberImageDto memberImageDto = new MemberImageDto(memberDto.getMemberId(), fileName, fileFullName, fileMap.get("serverName"), fileMap.get("url"), memberImage.getSize(), ext);
            
            memberService.createMemberImage(memberImageDto);
            // System.out.println("memberImageDto : "+memberImageDto.toString());
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
        @RequestPart @Valid MemberEditRequestDto memberEditDto,
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
    public ResponseEntity<MemberDetailsResponseDto> memberDetails(
        @ApiParam(value = "회원 시퀀스", required = true)int memberId,
        HttpServletRequest request
    ) throws Exception {

        // System.out.println("값 찍기 : "+request.getHeader("accessToken"));
        MemberDetailsResponseDto memberDetails = new MemberDetailsResponseDto("사용자 아이디", "사용자 이메일", "사용자 닉네임", "사용자 소개글", "사용자 이미지 경로");

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
        @RequestBody MemberLoginRequestDto memberLoginDto
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
    public ResponseEntity<MemberSerchResponseDto> memberDetailOther(
        @ApiParam(value = "회원 시퀀스", required = true)int memberId
    ) throws Exception {

        DailysDto dailysDto = new DailysDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);

        List<DailysDto> dailyList = new ArrayList<>();

        dailyList.add(dailysDto);

        RecipesDto recipesDto = new RecipesDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);

        List<RecipesDto> recipeList = new ArrayList<>();

        recipeList.add(recipesDto);

        MemberSerchResponseDto memberSerchDto = new MemberSerchResponseDto(dailyList, recipeList, "사용자 소개글 입니다.", "이미지 경로", 5);
        
        return new ResponseEntity<>(memberSerchDto, HttpStatus.OK);
    }

    // TODO : 토큰 재발큽 코드는 어떻게 진행할것인가?
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
    public ResponseEntity<MemberScrapResponseDto> scraps(
        @ApiParam(value = "memberId", required = true) int memberId
    ) throws Exception {

        DailysDto dailysDto = new DailysDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);

        List<DailysDto> dailyList = new ArrayList<>();

        dailyList.add(dailysDto);

        RecipesDto recipesDto = new RecipesDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);

        List<RecipesDto> recipeList = new ArrayList<>();

        recipeList.add(recipesDto);

        MemberScrapResponseDto memberScrap = new MemberScrapResponseDto(dailyList, recipeList);
        
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

    // TODO : 민택님이 수정해서 사용하시면 될거같습니다
    // @Operation(summary = "member food recommend", description = "회원 음식 추천 목록")
    // @ApiResponses({ 
    //         @ApiResponse(responseCode = "200", description = "OK"),
    //         @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
    //         @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    //         @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    // })
    // @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    // @ResponseBody
    // @GetMapping(value = "/member/food/{memberId}")
    // public ResponseEntity<List<FoodDtooo>> memberFood(
    //     @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
    // ) throws Exception {

    //     FoodDtooo foodDto = new FoodDtooo("짬뽕", "중식", LocalDateTime.now());

    //     List<FoodDtooo> foodList = new ArrayList<>();

    //     foodList.add(foodDto);

    //     return new ResponseEntity<>(foodList, HttpStatus.OK);
    // }

    // TODO : 민택님이 수정해서 사용하시면 될거같습니다
    // @Operation(summary = "member food List", description = "회원 음식 식단 리스트")
    // @ApiResponses({ 
    //         @ApiResponse(responseCode = "200", description = "OK"),
    //         @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
    //         @ApiResponse(responseCode = "404", description = "NOT FOUND"),
    //         @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    // })
    // @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    // @ResponseBody
    // @GetMapping(value = "/member/food/list/{memberId}")
    // public ResponseEntity<List<FoodRecommendDto>> memberFoodList(
    //     @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
    // ) throws Exception {

    //     FoodRecommendDto foodRecommendDto = new FoodRecommendDto("한식", "된장찌개", "중식", "짬뽕",
    //     "한식", "된장찌개", "중식", "짬뽕",
    //     "한식", "된장찌개", "중식", "짬뽕",
    //     "한식", "된장찌개", "중식", "짬뽕",
    //     "한식", "된장찌개", "중식", "짬뽕",
    //     "한식", "된장찌개", "중식", "짬뽕",
    //     "한식", "된장찌개", "중식", "짬뽕");

    //     List<FoodRecommendDto> foodList = new ArrayList<>();

    //     foodList.add(foodRecommendDto);

    //     return new ResponseEntity<>(foodList, HttpStatus.OK);
    // }


}
