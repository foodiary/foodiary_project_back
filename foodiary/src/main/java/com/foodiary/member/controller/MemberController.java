package com.foodiary.member.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.foodiary.auth.jwt.CustomUserDetails;
import com.foodiary.auth.service.UserService;
import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.daily.model.DailyDto;
import com.foodiary.daily.model.DailysResponseDto;
import com.foodiary.member.model.MemberCheckEmailNumRequestDto;
import com.foodiary.member.model.MemberCheckEmailRequestDto;
import com.foodiary.member.model.MemberCheckIdEmailRequestDto;
import com.foodiary.member.model.MemberCheckIdRequestDto;
import com.foodiary.member.model.MemberCheckNicknameRequestDto;
import com.foodiary.member.model.MemberCheckPwJwtRequestDto;
import com.foodiary.member.model.MemberDailyCommentDetailResponseDto;
import com.foodiary.member.model.MemberDailyCommentDto;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberEditPasswordRequestDto;
import com.foodiary.member.model.MemberEditRequestDto;
import com.foodiary.member.model.MemberFaqDto;
import com.foodiary.member.model.MemberFoodsResponseDto;
import com.foodiary.member.model.MemberNoticeInfoResponseDto;
import com.foodiary.member.model.MemberNoticeResponseDto;
import com.foodiary.member.model.MemberPostLikeResponseDto;
import com.foodiary.member.model.MemberPostScrapResponseDto;
import com.foodiary.member.model.MemberQuestionEditResponseDto;
import com.foodiary.member.model.MemberQuestionResponseDto;
import com.foodiary.member.model.MemberQuestionWriteResponseDto;
import com.foodiary.member.model.MemberRecipeCommentDetailResponseDto;
import com.foodiary.member.model.MemberRecipeCommentDto;
import com.foodiary.member.model.MemberSerchResponseDto;
import com.foodiary.member.model.MemberSignUpRequestDto;
import com.foodiary.member.service.MemberService;
import com.foodiary.recipe.model.RecipeDto;
import com.foodiary.recipe.model.RecipesResponseDto;
import com.github.pagehelper.PageHelper;

import io.jsonwebtoken.JwtException;
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

    @Operation(summary = "member password edit", description = "마이페이지에서 비밀번호 수정하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @PatchMapping(value = "/member/password/{memberId}")
    public ResponseEntity<?> memberModifyPassword(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId,
        @RequestBody @Valid MemberEditPasswordRequestDto memberEditPasswordRequestDto
    ) throws Exception {

        memberService.editMemberPassword(memberEditPasswordRequestDto, memberId);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member image edit", description = "마이페이지에서 이미지 수정하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @PatchMapping(value = "/member/image/{memberId}")
    public ResponseEntity<?> memberModifyImage(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId,
        @Parameter(description = "사진 이미지")
        @RequestPart(value = "memberImage", required = true) MultipartFile memberImage,
        @RequestPart(value = "memberPath", required = false) String memberPath
    ) throws Exception {

        memberService.editMemberImage(memberId, memberImage, memberPath);

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
    @ApiImplicitParam(name = "jwt", value = "JWT Token", required = true, dataType = "string", paramType = "header")
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
        @RequestPart(value = "memberSignUpDto", required = true) @Valid MemberSignUpRequestDto memberSignUpDto,
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
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @PatchMapping(value = "/member/{memberId}")
    public ResponseEntity<String> memberModify(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId,
        @RequestBody @Valid MemberEditRequestDto memberEditDto
    ) throws Exception {

        memberService.updateMember(memberEditDto, memberId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member info", description = "회원 정보 보기(본인꺼) 마이페이지")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member/{memberId}")
    public ResponseEntity<MemberDto> memberDetails(
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId
    ) throws Exception {

        MemberDto memberdto = memberService.findByMemberIdInfo(memberId);
        return new ResponseEntity<>(memberdto, HttpStatus.OK);
    }

    @Operation(summary = "member delete", description = "회원 탈퇴")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @DeleteMapping(value = "/member/{memberId}")
    public ResponseEntity<String> memberDelete(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId,
        @AuthenticationPrincipal CustomUserDetails memberDetails,
        @RequestHeader("Authorization") String bearerAtk) throws JwtException
    {
        memberService.deleteMember(memberId);

        userService.memberLogout(memberDetails, bearerAtk);
        
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    // TODO : 디자인 나오고 수정해야할 부분
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

        DailysResponseDto dailysResponseDto = new DailysResponseDto(1, "제목입니다.", "작성자", "경로입니다.", 1, 2, LocalDateTime.now(), 5);

        List<DailysResponseDto> dailyList = new ArrayList<>();

        dailyList.add(dailysResponseDto);

        RecipesResponseDto recipesResponseDto = new RecipesResponseDto(1, "제목입니다.", "작성자", "경로입니다.", 1, 2, LocalDateTime.now(), 5);

        List<RecipesResponseDto> recipeList = new ArrayList<>();

        recipeList.add(recipesResponseDto);

        MemberSerchResponseDto memberSerchDto = new MemberSerchResponseDto(dailyList, recipeList, "사용자 소개글 입니다.", "이미지 경로", 5);

        return new ResponseEntity<>(memberSerchDto, HttpStatus.OK);
    }

    @Operation(summary = "member post view", description = "본인이 쓴 하루 식단 게시글 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @GetMapping(value = "/member/post/daily/{memberId}")
    public ResponseEntity<List<DailyDto>> memberViewDailyPosts(
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId,
        @ApiParam(value="페이지", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<DailyDto> dailyList = memberService.postDailyFind(memberId);

        return new ResponseEntity<>(dailyList, HttpStatus.OK);
    }

    @Operation(summary = "member post view", description = "본인이 쓴 레시피 게시글 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @GetMapping(value = "/member/post/recipe/{memberId}")
    public ResponseEntity<List<RecipeDto>> memberViewRecipePosts(
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId,
        @ApiParam(value="페이지", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<RecipeDto> recipeList = memberService.postRecipeFind(memberId);

        return new ResponseEntity<>(recipeList, HttpStatus.OK);
    }

    @Operation(summary = "member image delete", description = "회원 이미지 삭제")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @DeleteMapping(value = "/member/image/{memberId}")
    public ResponseEntity<String> memberImageDelete(
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId
    ) throws Exception {

        memberService.deleteMemberImage(memberId);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member scrap list", description = "회원(본인) 스크랩 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member/scrap/{memberId}")
    public ResponseEntity<List<MemberPostScrapResponseDto> > postScraps(
        @PathVariable @ApiParam(value = "memberId", required = true) int memberId,
        @ApiParam(value="페이지", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberPostScrapResponseDto>  memberPostScrapResponseDtoList= memberService.detailScrap(memberId);

        return new ResponseEntity<>(memberPostScrapResponseDtoList, HttpStatus.OK);
    }

    @Operation(summary = "member like list", description = "회원(본인) 좋아요 한 글 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member/like/{memberId}")
    public ResponseEntity<List<MemberPostLikeResponseDto>> postLikes(
        @PathVariable @ApiParam(value = "멤버 시퀀스", required = true) int memberId,
        @ApiParam(value="페이지", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberPostLikeResponseDto> memberPostLikeResponseDtoList = memberService.detailLike(memberId);

        return new ResponseEntity<>(memberPostLikeResponseDtoList, HttpStatus.OK);
    }

    @Operation(summary = "member comment list", description = "회원(본인)이 쓴 하루식단 댓글 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member/comment/daily/{memberId}")
    public ResponseEntity<List<MemberDailyCommentDto>> dailyComments(
        @PathVariable @ApiParam(value = "멤버 시퀀스", required = true) int memberId,
        @ApiParam(value="페이지", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberDailyCommentDto> dailyList = memberService.commentDailyList(memberId);

        return new ResponseEntity<>(dailyList, HttpStatus.OK);
    }

    @Operation(summary = "member comment list", description = "회원(본인)이 쓴 레시피 댓글 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member/comment/recipe/{memberId}")
    public ResponseEntity<List<MemberRecipeCommentDto>> recipeComments(
        @PathVariable @ApiParam(value = "멤버 시퀀스", required = true) int memberId,
        @ApiParam(value="페이지", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberRecipeCommentDto> recipeList = memberService.commentRecipeList(memberId);

        return new ResponseEntity<>(recipeList, HttpStatus.OK);
    }

    @Operation(summary = "member comment list", description = "회원(본인)이 쓴 하루식단 댓글 상세 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member/comment/daily/{memberId}/{dailyId}/{dailyCommentId}")
    public ResponseEntity<MemberDailyCommentDetailResponseDto> dailyCommentsDetail(
        @PathVariable @ApiParam(value = "멤버 시퀀스", required = true) int memberId,
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int dailyId,
        @PathVariable @ApiParam(value = "댓글 시퀀스", required = true) int dailyCommentId
    ) throws Exception {

        MemberDailyCommentDetailResponseDto memberDailyCommentDetailResponseDto = memberService.commentDailyDetail(memberId, dailyId, dailyCommentId);

        return new ResponseEntity<>(memberDailyCommentDetailResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "member comment list", description = "회원(본인)이 쓴 레시피 댓글 상세 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member/comment/recipe/{memberId}/{recipeId}/{recipeCommentId}")
    public ResponseEntity<MemberRecipeCommentDetailResponseDto> recipeCommentsDetail(
        @PathVariable @ApiParam(value = "멤버 시퀀스", required = true) int memberId,
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int recipeId,
        @PathVariable @ApiParam(value = "댓글 시퀀스", required = true) int recipeCommentId
    ) throws Exception {

        MemberRecipeCommentDetailResponseDto memberRecipeCommentDetailResponseDto = memberService.commentRecipeDetail(memberId, recipeId, recipeCommentId);

        return new ResponseEntity<>(memberRecipeCommentDetailResponseDto, HttpStatus.OK);
    }


    @Operation(summary = "notice list", description = "공지사항 보기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/notice")
    public ResponseEntity<List<MemberNoticeResponseDto>> notices(
        @ApiParam(value="페이지", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberNoticeResponseDto> memberNoticeDtoList = memberService.noticeList();
        
        return new ResponseEntity<>(memberNoticeDtoList, HttpStatus.OK);
    }

    @Operation(summary = "notice list", description = "공지사항 상세보기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/notice/{noticeId}")
    public ResponseEntity<MemberNoticeInfoResponseDto> noticeView(
        @PathVariable @ApiParam(value = "공지 시퀀스", required = true) int noticeId
    ) throws Exception {

        MemberNoticeInfoResponseDto memberNoticeInfoResponseDto = memberService.noticeDetail(noticeId);
        
        return new ResponseEntity<>(memberNoticeInfoResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "faq list", description = "faq 보기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/faq")
    public ResponseEntity<List<MemberFaqDto>> faqs(
        @ApiParam(value="페이지", required = true) @Positive int page
    ) throws Exception {

        if(page <=0) {
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberFaqDto> memberFaqDtoList = memberService.faqList();
        
        return new ResponseEntity<>(memberFaqDtoList, HttpStatus.OK);
    }

    @Operation(summary = "question list", description = "문의 내역 확인")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @GetMapping(value = "/question/{memberId}")
    public ResponseEntity<List<MemberQuestionResponseDto>> question(
        @PathVariable @ApiParam(value = "멤버 시퀀스", required = true) int memberId,
        @ApiParam(value="페이지", required = true) @Positive int page
    ) throws Exception {

        if(page <=0) {
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberQuestionResponseDto> memberQuestionResponseDtoList = memberService.questionList(memberId);
        
        return new ResponseEntity<>(memberQuestionResponseDtoList, HttpStatus.OK);
    }

    @Operation(summary = "question detail", description = "문의 상세보기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @GetMapping(value = "/question/{memberId}/{questionId}")
    public ResponseEntity<MemberQuestionResponseDto> questionDetail(
        @PathVariable @ApiParam(value = "멤버 시퀀스", required = true) int memberId,
        @PathVariable @ApiParam(value = "문의 시퀀스", required = true) int questionId
    ) throws Exception {

        MemberQuestionResponseDto memberQuestionResponseDto = memberService.questionDetail(memberId, questionId);
        
        return new ResponseEntity<>(memberQuestionResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "question write", description = "question 작성하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @PostMapping(value = "/question")
    public ResponseEntity<String> questionPost(
        @RequestPart(value = "memberQuestionWriteResponseDto", required = true) @Valid MemberQuestionWriteResponseDto memberQuestionWriteResponseDto,
        @RequestPart(value = "memberImage", required = false) MultipartFile memberImage
    ) throws Exception {

        memberService.questionWrite(memberQuestionWriteResponseDto, memberImage);
        
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "question edit", description = "question 수정")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @PatchMapping(value = "/question/{memberId}/{questionId}")
    public ResponseEntity<String> questionModify(
        @PathVariable @ApiParam(value = "멤버 시퀀스", required = true) int memberId,
        @PathVariable @ApiParam(value = "문의 시퀀스", required = true) int questionId,
        @RequestPart(value = "memberQuestionEditResponseDto", required = true) @Valid MemberQuestionEditResponseDto memberQuestionEditResponseDto,
        @RequestPart(value = "memberImage", required = false) MultipartFile memberImage
    ) throws Exception {

        memberService.questionEdit(memberId, questionId, memberQuestionEditResponseDto, memberImage);
        
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "question list", description = "question 삭제")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @DeleteMapping(value = "/question/{memberId}/{questionId}")
    public ResponseEntity<String> questionDelete(
        @PathVariable @ApiParam(value = "멤버 시퀀스", required = true) int memberId,
        @PathVariable @ApiParam(value = "문의 시퀀스", required = true) int questionId
    ) throws Exception {

        memberService.deleteQeustion(memberId, questionId);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member food recommend", description = "회원 음식 추천 목록")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @GetMapping(value = "/member/food/{memberId}")
    public ResponseEntity<List<MemberFoodsResponseDto>> memberFood(
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId,
        @ApiParam(value="페이지", required = true) @Positive int page
    ) throws Exception {

        if(page <=0) {
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberFoodsResponseDto> foodList = memberService.foods(memberId);

        return new ResponseEntity<>(foodList, HttpStatus.OK);
    }





}
