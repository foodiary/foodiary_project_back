package com.foodiary.member.controller;

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
import com.foodiary.member.model.MemberCheckEmailNumRequestDto;
import com.foodiary.member.model.MemberCheckEmailRequestDto;
import com.foodiary.member.model.MemberCheckIdEmailRequestDto;
import com.foodiary.member.model.MemberCheckIdRequestDto;
import com.foodiary.member.model.MemberCheckNicknameRequestDto;
import com.foodiary.member.model.MemberCheckPwJwtRequestDto;
import com.foodiary.member.model.MemberDailyCommentDetailResponseDto;
import com.foodiary.member.model.MemberDailyCommentDto;
import com.foodiary.member.model.MemberDailyResponseDto;
import com.foodiary.member.model.MemberEditPasswordRequestDto;
import com.foodiary.member.model.MemberEditRequestDto;
import com.foodiary.member.model.MemberFaqDto;
import com.foodiary.member.model.MemberFoodsResponseDto;
import com.foodiary.member.model.MemberNoticeInfoResponseDto;
import com.foodiary.member.model.MemberNoticeResponseDto;
import com.foodiary.member.model.MemberOtherMemberResponseDto;
import com.foodiary.member.model.MemberPostLikeResponseDto;
import com.foodiary.member.model.MemberPostScrapResponseDto;
import com.foodiary.member.model.MemberQuestionDetailResponseDto;
import com.foodiary.member.model.MemberQuestionEditResponseDto;
import com.foodiary.member.model.MemberQuestionResponseDto;
import com.foodiary.member.model.MemberQuestionWriteResponseDto;
import com.foodiary.member.model.MemberRecipeCommentDetailResponseDto;
import com.foodiary.member.model.MemberRecipeCommentDto;
import com.foodiary.member.model.MemberResponseDto;
import com.foodiary.member.model.MemberSignUpRequestDto;
import com.foodiary.member.service.MemberService;
import com.foodiary.recipe.model.RecipeDto;
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

    // ??????????????? ???????????? ??????, ?????? ??????
    @ResponseBody
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @Operation(summary = "member password edit", description = "????????????????????? ???????????? ????????????")
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
        @PathVariable @ApiParam(value = "?????? ?????????")int memberId,
        @RequestBody @Valid MemberEditPasswordRequestDto memberEditPasswordRequestDto
    ) throws Exception {

        memberService.editMemberPassword(memberEditPasswordRequestDto, memberId);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member image edit", description = "????????????????????? ????????? ????????????")
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
        @PathVariable @ApiParam(value = "?????? ?????????")int memberId,
        @Parameter(description = "?????? ?????????")
        @RequestPart(value = "memberImage", required = true) MultipartFile memberImage,
        @RequestPart(value = "memberPath", required = false) String memberPath
    ) throws Exception {

        memberService.editMemberImage(memberId, memberImage, memberPath);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member password Find", description = "???????????? ??????")
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

    @Operation(summary = "member password jwt confirm", description = "jwt??? ???????????? ????????????")
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

    @Operation(summary = "member id Find", description = "????????? ??????")
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


    @Operation(summary = "member id check", description = "????????? ?????? ??????")
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

    @Operation(summary = "member nickname check", description = "????????? ?????? ??????")
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

    @Operation(summary = "member email check", description = "????????? ?????? ??????")
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

    @Operation(summary = "member email send, identity verification", description = "????????? ?????? ?????? ??????")
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

    @Operation(summary = "member verification num confirm", description = "????????? ?????? ??? ???????????? ??????")
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

    @Operation(summary = "member sign up", description = "?????? ????????????")
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
        @Parameter(description = "?????? ?????????")
        @RequestPart(value = "memberImage", required = false) MultipartFile memberImage
    ) throws Exception {

        memberService.createdMember(memberSignUpDto, memberImage);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member info modify", description = "?????? ?????? ??????")
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
        @PathVariable @ApiParam(value = "?????? ?????????")int memberId,
        @RequestBody @Valid MemberEditRequestDto memberEditDto
    ) throws Exception {

        memberService.updateMember(memberEditDto, memberId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member info", description = "?????? ?????? ??????(?????????) ???????????????")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/member/{memberId}")
    public ResponseEntity<MemberResponseDto> memberDetails(
        @PathVariable @ApiParam(value = "?????? ?????????", required = true)int memberId
    ) throws Exception {

        MemberResponseDto memberdto = memberService.findByMemberIdInfo(memberId);
        return new ResponseEntity<>(memberdto, HttpStatus.OK);
    }

    @Operation(summary = "member delete", description = "?????? ??????")
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
        @PathVariable @ApiParam(value = "?????? ?????????")int memberId,
        @AuthenticationPrincipal CustomUserDetails memberDetails,
        @RequestHeader("Authorization") String bearerAtk) throws JwtException
    {
        memberService.deleteMember(memberId);

        userService.memberLogout(memberDetails, bearerAtk);
        
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member search", description = "?????? ?????? ??????(?????? ?????? ??? ?????? ????????? ??????)")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/member/search/{memberId}")
    public ResponseEntity<MemberOtherMemberResponseDto> memberDetailOther(
        @PathVariable @ApiParam(value = "????????? ????????? ?????????", required = true)int memberId,
        @ApiParam(value="?????????", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        MemberOtherMemberResponseDto memberOtherMemberResponseDto = memberService.findMember(memberId);
        
        return new ResponseEntity<>(memberOtherMemberResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "member post view", description = "????????? ??? ?????? ?????? ????????? ??????")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @GetMapping(value = "/member/post/daily/{memberId}")
    public ResponseEntity<List<MemberDailyResponseDto>> memberViewDailyPosts(
        @PathVariable @ApiParam(value = "?????? ?????????", required = true)int memberId,
        @ApiParam(value="?????????", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberDailyResponseDto> dailyList = memberService.postDailyFind(memberId);

        return new ResponseEntity<>(dailyList, HttpStatus.OK);
    }

    @Operation(summary = "member post view", description = "????????? ??? ????????? ????????? ??????")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    // @GetMapping(value = "/member/post/recipe/{memberId}")
    public ResponseEntity<List<RecipeDto>> memberViewRecipePosts(
        @PathVariable @ApiParam(value = "?????? ?????????", required = true)int memberId,
        @ApiParam(value="?????????", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<RecipeDto> recipeList = memberService.postRecipeFind(memberId);

        return new ResponseEntity<>(recipeList, HttpStatus.OK);
    }

    @Operation(summary = "member image delete", description = "?????? ????????? ??????")
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
        @PathVariable @ApiParam(value = "?????? ?????????", required = true)int memberId
    ) throws Exception {

        memberService.deleteMemberImage(memberId);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member scrap list", description = "??????(??????) ????????? ??????")
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
        @ApiParam(value="?????????", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberPostScrapResponseDto> memberPostScrapResponseDtoList= memberService.detailScrap(memberId);

        return new ResponseEntity<>(memberPostScrapResponseDtoList, HttpStatus.OK);
    }

    @Operation(summary = "member like list", description = "??????(??????) ????????? ??? ??? ??????")
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
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int memberId,
        @ApiParam(value="?????????", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberPostLikeResponseDto> memberPostLikeResponseDtoList = memberService.detailLike(memberId);

        return new ResponseEntity<>(memberPostLikeResponseDtoList, HttpStatus.OK);
    }

    @Operation(summary = "member comment list", description = "??????(??????)??? ??? ???????????? ?????? ??????")
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
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int memberId,
        @ApiParam(value="?????????", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberDailyCommentDto> dailyList = memberService.commentDailyList(memberId);

        return new ResponseEntity<>(dailyList, HttpStatus.OK);
    }

    @Operation(summary = "member comment list", description = "??????(??????)??? ??? ????????? ?????? ??????")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    // @GetMapping(value = "/member/comment/recipe/{memberId}")
    public ResponseEntity<List<MemberRecipeCommentDto>> recipeComments(
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int memberId,
        @ApiParam(value="?????????", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberRecipeCommentDto> recipeList = memberService.commentRecipeList(memberId);

        return new ResponseEntity<>(recipeList, HttpStatus.OK);
    }

    @Operation(summary = "member comment list", description = "??????(??????)??? ??? ???????????? ?????? ?????? ??????")
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
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int memberId,
        @PathVariable @ApiParam(value = "????????? ?????????", required = true) int dailyId,
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int dailyCommentId
    ) throws Exception {

        MemberDailyCommentDetailResponseDto memberDailyCommentDetailResponseDto = memberService.commentDailyDetail(memberId, dailyId, dailyCommentId);

        return new ResponseEntity<>(memberDailyCommentDetailResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "member comment list", description = "??????(??????)??? ??? ????????? ?????? ?????? ??????")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    // @GetMapping(value = "/member/comment/recipe/{memberId}/{recipeId}/{recipeCommentId}")
    public ResponseEntity<MemberRecipeCommentDetailResponseDto> recipeCommentsDetail(
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int memberId,
        @PathVariable @ApiParam(value = "????????? ?????????", required = true) int recipeId,
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int recipeCommentId
    ) throws Exception {

        MemberRecipeCommentDetailResponseDto memberRecipeCommentDetailResponseDto = memberService.commentRecipeDetail(memberId, recipeId, recipeCommentId);

        return new ResponseEntity<>(memberRecipeCommentDetailResponseDto, HttpStatus.OK);
    }


    @Operation(summary = "notice list", description = "???????????? ??????")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/notice")
    public ResponseEntity<List<MemberNoticeResponseDto>> notices(
        @ApiParam(value="?????????", required = true) @Positive int page
    ) throws Exception {

        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberNoticeResponseDto> memberNoticeDtoList = memberService.noticeList();
        
        return new ResponseEntity<>(memberNoticeDtoList, HttpStatus.OK);
    }

    @Operation(summary = "notice list", description = "???????????? ????????????")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/notice/{noticeId}")
    public ResponseEntity<MemberNoticeInfoResponseDto> noticeView(
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int noticeId
    ) throws Exception {

        MemberNoticeInfoResponseDto memberNoticeInfoResponseDto = memberService.noticeDetail(noticeId);
        
        return new ResponseEntity<>(memberNoticeInfoResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "faq list", description = "faq ??????")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/faq")
    public ResponseEntity<List<MemberFaqDto>> faqs(
        @ApiParam(value="?????????", required = true) @Positive int page
    ) throws Exception {

        if(page <=0) {
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberFaqDto> memberFaqDtoList = memberService.faqList();
        
        return new ResponseEntity<>(memberFaqDtoList, HttpStatus.OK);
    }

    @Operation(summary = "question list", description = "?????? ?????? ??????")
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
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int memberId,
        @ApiParam(value="?????????", required = true) @Positive int page
    ) throws Exception {

        if(page <=0) {
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberQuestionResponseDto> memberQuestionResponseDtoList = memberService.questionList(memberId);
        
        return new ResponseEntity<>(memberQuestionResponseDtoList, HttpStatus.OK);
    }

    @Operation(summary = "question detail", description = "?????? ????????????")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @GetMapping(value = "/question/{memberId}/{questionId}")
    public ResponseEntity<MemberQuestionDetailResponseDto> questionDetail(
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int memberId,
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int questionId
    ) throws Exception {

        MemberQuestionDetailResponseDto memberQuestionResponseDto = memberService.questionDetail(memberId, questionId);
        
        return new ResponseEntity<>(memberQuestionResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "question write", description = "question ????????????")
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

    @Operation(summary = "question edit", description = "question ??????")
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
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int memberId,
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int questionId,
        @RequestPart(value = "memberQuestionEditResponseDto", required = true) @Valid MemberQuestionEditResponseDto memberQuestionEditResponseDto,
        @RequestPart(value = "memberImage", required = false) MultipartFile memberImage
    ) throws Exception {

        memberService.questionEdit(memberId, questionId, memberQuestionEditResponseDto, memberImage);
        
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "question list", description = "question ??????")
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
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int memberId,
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int questionId
    ) throws Exception {

        memberService.deleteQeustion(memberId, questionId);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "member food recommend", description = "?????? ?????? ?????? ??????")
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
        @PathVariable @ApiParam(value = "?????? ?????????", required = true) int memberId,
        @ApiParam(value="?????????", required = true) @Positive int page
    ) throws Exception {

        if(page <=0) {
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);
        List<MemberFoodsResponseDto> foodList = memberService.foods(memberId);

        return new ResponseEntity<>(foodList, HttpStatus.OK);
    }





}
