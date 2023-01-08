package com.foodiary.member.controller;

import java.io.IOException;
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

import com.foodiary.common.email.EmailService;
import com.foodiary.common.s3.S3Service;
import com.foodiary.daily.model.DailysResponseDto;
import com.foodiary.member.model.MemberCheckEmailNumRequestDto;
import com.foodiary.member.model.MemberCheckEmailRequestDto;
import com.foodiary.member.model.MemberCheckIdEmailRequestDto;
import com.foodiary.member.model.MemberCheckIdRequestDto;
import com.foodiary.member.model.MemberCheckNicknameRequestDto;
import com.foodiary.member.model.MemberCheckPwJwtRequestDto;
import com.foodiary.member.model.MemberEditPasswordRequestDto;
import com.foodiary.member.model.MemberEditRequestDto;
import com.foodiary.member.model.MemberEditResponseDto;
import com.foodiary.member.model.MemberLikeResponseDto;
import com.foodiary.member.model.MemberScrapResponseDto;
import com.foodiary.member.model.MemberSerchResponseDto;
import com.foodiary.member.model.MemberSignUpRequestDto;
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

    private final EmailService emailService;

    private final S3Service s3Service;

    @GetMapping("/email/test")
    @ResponseBody
    public String emailTest() throws IOException{
        // emailService.EmailSend();
        s3Service.deleteImage("member/1c7ff3c4-0a59-4d2a-91c0-4e85de9603381672406079088.png");
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
        @RequestBody @Valid MemberEditPasswordRequestDto memberEditPasswordRequestDto
    ) throws Exception {

        memberService.EditMemberPassWord(memberEditPasswordRequestDto.getPassword(), memberId);

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
        @RequestBody @Valid MemberCheckIdEmailRequestDto memberCheckIdEmailRequestDto
    ) throws Exception {

        memberService.findmemberInfoPw(memberCheckIdEmailRequestDto.getEmail(), memberCheckIdEmailRequestDto.getLoginId(), "pw");

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member password jwt confirm", description = "jwt로 비밀번호 변경하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/member/password/change/jwt")
    public ResponseEntity<?> memberPasswordConfirm(
        @RequestBody @Valid MemberCheckPwJwtRequestDto memberCheckPwJwtRequestDto
    ) throws Exception {

        memberService.memberPwConfirm(memberCheckPwJwtRequestDto);

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
        @RequestBody @Valid MemberCheckEmailRequestDto memberCheckEmailRequestDto
    ) throws Exception {

        memberService.findmemberInfoId(memberCheckEmailRequestDto.getEmail(), "id");

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
        @RequestBody @Valid MemberCheckIdRequestDto memberCheckIdRequestDto
    ) throws Exception {

        memberService.findMemberLoginId(memberCheckIdRequestDto.getLoginId());

        return new ResponseEntity<>("OK", HttpStatus.OK);
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
        @RequestBody @Valid MemberCheckNicknameRequestDto memberCheckNicknameRequestDto
    ) throws Exception {

        memberService.findmemberNickname(memberCheckNicknameRequestDto.getNickName());

        return new ResponseEntity<>("OK", HttpStatus.OK);
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
        @RequestBody @Valid MemberCheckEmailRequestDto memberCheckEmailRequestDto
    ) throws Exception {

        memberService.findmemberEmail(memberCheckEmailRequestDto.getEmail());

        return new ResponseEntity<>("OK", HttpStatus.OK);
        
    }

    @Operation(summary = "member email send, identity verification", description = "이메일 발송 본인 인증")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/member/email/send")
    public ResponseEntity<?> memberEmailSend(
        @RequestBody @Valid MemberCheckEmailRequestDto memberCheckEmailRequestDto
    ) throws Exception {

        memberService.mailSend(memberCheckEmailRequestDto);
        return new ResponseEntity<>("OK", HttpStatus.OK);
        
    }

    @Operation(summary = "member verification num confirm", description = "이메일 발송 후 인증번호 확인")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/member/email/send/confirm")
    public ResponseEntity<?> memberEmailSendConfirm(
        @RequestBody MemberCheckEmailNumRequestDto memberCheckEmailNumRequestDto
    ) throws Exception {

        memberService.mailSendConfirm(memberCheckEmailNumRequestDto);
        return new ResponseEntity<>("OK", HttpStatus.OK);
        
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

        memberService.createdMember(memberSignUpDto, memberImage);

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

        memberService.updateMember(memberEditDto, memberId, memberImage);
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
    public ResponseEntity<MemberEditResponseDto> memberDetails(
        @ApiParam(value = "회원 시퀀스", required = true)int memberId,
        HttpServletRequest request
    ) throws Exception {

        MemberEditResponseDto memberEditResponseDto = memberService.findByMemberIdInfo(memberId);
        // System.out.println("값 찍기 : "+request.getHeader("accessToken"));

        return new ResponseEntity<>(memberEditResponseDto, HttpStatus.OK);
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

        memberService.deleteMember(memberId);
        
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

        DailysResponseDto dailysResponseDto = new DailysResponseDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);

        List<DailysResponseDto> dailyList = new ArrayList<>();

        dailyList.add(dailysResponseDto);

        RecipesResponseDto recipesResponseDto = new RecipesResponseDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);

        List<RecipesResponseDto> recipeList = new ArrayList<>();

        recipeList.add(recipesResponseDto);

        MemberSerchResponseDto memberSerchDto = new MemberSerchResponseDto(dailyList, recipeList, "사용자 소개글 입니다.", "이미지 경로", 5);
        
        return new ResponseEntity<>(memberSerchDto, HttpStatus.OK);
    }

    @Operation(summary = "member image delete", description = "회원 이미지 삭제")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @DeleteMapping(value = "/member/image/{memberId}")
    public ResponseEntity<String> memberImageDelete(
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId
    ) throws Exception {

        memberService.deleteMemberImage(memberId);

        return new ResponseEntity<>("OK", HttpStatus.OK);
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

//     @Operation(summary = "member scrap list", description = "회원(본인) 스크랩 조회")
//     @ApiResponses({
//             @ApiResponse(responseCode = "200", description = "OK"),
//             @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
//             @ApiResponse(responseCode = "404", description = "NOT FOUND"),
//             @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
//     })
//     @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
//     @ResponseBody
//     @GetMapping(value = "/member/scrap")
//     public ResponseEntity<MemberScrapResponseDto> scraps(
//         @ApiParam(value = "memberId", required = true) int memberId
//     ) throws Exception {
//
//         DailysResponseDto dailysResponseDto = new DailysResponseDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);
//
//         List<DailysResponseDto> dailyList = new ArrayList<>();
//
//         dailyList.add(dailysResponseDto);
//
//         RecipesResponseDto recipesResponseDto = new RecipesResponseDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);
//
//         List<RecipesResponseDto> recipeList = new ArrayList<>();
//
//         recipeList.add(recipesResponseDto);
//
//         MemberScrapDto memberScrap = new MemberScrapDto(dailyList, recipeList);
//
//         return new ResponseEntity<>(memberScrap, HttpStatus.OK);
//     }

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

        memberService.deleteScrapDaily(scrapId, memberId);

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

        memberService.deleteScrapRecipe(scrapId, memberId);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member like list", description = "회원(본인) 좋아요 한 글 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member/like")
    public ResponseEntity<MemberLikeResponseDto> likes(
        @ApiParam(value = "멤버 시퀀스", required = true) int memberId
    ) throws Exception {

        // TODO : 좋아요, 조회수, 댓글수 모두 포함        
        MemberLikeResponseDto memberLikeResponseDto = memberService.detailLike(memberId);

        return new ResponseEntity<>(memberLikeResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "member daily like delete", description = "회원 하루식단 좋아요 삭제")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @DeleteMapping(value = "/member/like/daily/{likeId}/{memberId}")
    public ResponseEntity<String> likeDailyDelete(
        @PathVariable @ApiParam(value = "좋아요 시퀀스", required = true) int likeId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId
    ) throws Exception {

        memberService.deleteLikeDaily(likeId, memberId);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member recipe like delete", description = "회원 레시피 좋아요 삭제")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @DeleteMapping(value = "/member/like/recipe/{likeId}/{memberId}")
    public ResponseEntity<String> likeRecipeDelete(
        @PathVariable @ApiParam(value = "좋아요 시퀀스", required = true) int likeId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId
    ) throws Exception {

        memberService.deleteLikeRecipe(likeId, memberId);

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
