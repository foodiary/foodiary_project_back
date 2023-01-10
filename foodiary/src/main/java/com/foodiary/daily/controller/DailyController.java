package com.foodiary.daily.controller;

import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.daily.model.*;
import com.foodiary.daily.service.DailyService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DailyController {

    private final DailyService dailyService;


    
    @Operation(summary = "daily write", description = "하루 식단 게시글 작성")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @PostMapping(value = "/daily")
    public ResponseEntity<?> dailyWrite(
        @RequestPart(value = "dailyWrite") @Valid DailyWriteRequestDto dailyWriteRequestDto,
        @Parameter(description = "사진 이미지")
        @RequestPart(value = "dailyImage", required = true) MultipartFile dailyImage
    ) throws Exception {

        dailyService.addDaily(dailyWriteRequestDto, dailyImage);

        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }




    @Operation(summary = "daily modify", description = "하루 식단 게시글 수정")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @PostMapping(value = "/daily/{dailyId}/{memberId}")
    public ResponseEntity<String> dailyModify(
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int dailyId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId,
        @RequestPart("dailyEdit") DailyEditRequestDto dailyEditRequestDto,
        @Parameter(description = "사진 이미지")
        @RequestPart(value = "dailyImage", required = false) MultipartFile dailyImage
    ) throws Exception {
        dailyEditRequestDto.setDailyId(dailyId);
        dailyEditRequestDto.setMemberId(memberId);
        dailyService.modifyDaily(dailyEditRequestDto, dailyImage);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }



    @Operation(summary = "daily list", description = "하루 식단 게시판 보기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping(value = "/dailys")
    public ResponseEntity<?> dailys(
            @ApiParam(value = "게시판 페이지", required = false) @Positive int page
    ) throws Exception {
        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);


        List<DailysResponseDto> response = dailyService.findDailys();
        return new ResponseEntity<>(PageInfo.of(response), HttpStatus.OK);
    }




    @Operation(summary = "today daily list", description = "일간 하루 식단 게시판 보기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping(value = "/dailys/today")
    public ResponseEntity<?> todayDailys(
        @ApiParam(value = "게시판 페이지", required = false) @Positive int page
    ) throws Exception {
        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));

        List<DailysResponseDto> response = dailyService.findCreateDailys(start, end);
        return new ResponseEntity<>(PageInfo.of(response), HttpStatus.OK);
    }

    @Operation(summary = "week daily list", description = "주간 하루 식단 게시판 보기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping(value = "/dailys/week")
    public ResponseEntity<?> weekDailys(
            @ApiParam(value = "게시판 페이지", required = false) @Positive int page
    ) throws Exception {
        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);

        LocalDateTime start = LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.of(0, 0, 0));
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));

        List<DailysResponseDto> response = dailyService.findCreateDailys(start, end);
        return new ResponseEntity<>(PageInfo.of(response), HttpStatus.OK);
    }

    @Operation(summary = "month daily list", description = "월간 하루 식단 게시판 보기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping(value = "/dailys/month")
    public ResponseEntity<?> monthDailys(
            @ApiParam(value = "게시판 페이지", required = false) @Positive int page
    ) throws Exception {
        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(page, 10);

        LocalDateTime start = LocalDateTime.of(LocalDate.now().minusDays(30), LocalTime.of(0, 0, 0));
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));

        List<DailysResponseDto> response = dailyService.findCreateDailys(start, end);
        return new ResponseEntity<>(PageInfo.of(response), HttpStatus.OK);
    }



    @Operation(summary = "TOP10 daily list", description = "하루 식단 게시판 최신글 10개 보기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping(value = "/dailys/top")
    public ResponseEntity<?> topDailys() throws Exception {

        List<DailysResponseDto> response = dailyService.findDailys();
        return new ResponseEntity<>(PageInfo.of(response), HttpStatus.OK);
    }

    @Operation(summary = "daily list detail", description = "하루 식단 게시글 상세 보기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/daily/datils")
    public ResponseEntity<DailyDetailsResponseDto> getDailyDetails (
        @ApiParam(value = "게시글 시퀀스", required = true) @Positive int dailyId
    ) throws Exception {

        DailyDetailsResponseDto response = dailyService.findDaily(dailyId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @Operation(summary = "daily delete", description = "하루 식단 게시글 삭제")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @DeleteMapping(value = "/daily/{dailyId}/{memberId}")
    public ResponseEntity<String> dailyDelete(
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int dailyId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId
    ) throws Exception {
        dailyService.removeDaily(dailyId, memberId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @Operation(summary = "daily comment write", description = "하루 식단 게시글 댓글 작성")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/daily/comment")
    public ResponseEntity<String> dailyCommentWrite(@RequestBody DailyCommentWriteRequestDto dailyCommentWriteRequestDto)
            throws Exception {
        dailyService.addDailyComment(dailyCommentWriteRequestDto);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }




    @Operation(summary = "daily comment view", description = "하루 식단 게시글 댓글 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/daily/comment")
    public ResponseEntity<?> dailyCommentDetails(
        @ApiParam(value = "게시글 시퀀스", required = true) @Positive int dailyId,
        @ApiParam(value = "댓글 페이지", required = true) @Positive int page
    ) throws Exception {
        if(page <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }

        PageHelper.startPage(page, 10);

        List<DailyCommentDetailsResponseDto> response = dailyService.findDailyComments(dailyId);
        return new ResponseEntity<>(PageInfo.of(response), HttpStatus.OK);
    }

    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @Operation(summary = "daily comment modify", description = "하루 식단 게시글 댓글 수정")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PatchMapping(value = "/daily/comment//{dailyId}/{memberId}/{commentId}")
    public ResponseEntity<String> dailyCommentModify(
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int dailyId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId,
        @PathVariable @ApiParam(value = "댓글 시퀀스", required = true) @Positive int commentId,
        @RequestBody DailyCommentEditRequestDto dailyCommentEditRequestDto
    ) throws Exception {
        dailyCommentEditRequestDto.setDailyId(dailyId);
        dailyCommentEditRequestDto.setMemberId(memberId);
        dailyCommentEditRequestDto.setCommentId(commentId);
        dailyService.modifyDailyComment(dailyCommentEditRequestDto);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @Operation(summary = "daily comment delete", description = "하루 식단 게시글 댓글 삭제")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @DeleteMapping(value = "/daily/comment/{dailyId}/{commentId}/{memberId}")
    public ResponseEntity<String> dailyCommentRemove(
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int dailyId,
        @PathVariable @ApiParam(value = "댓글 시퀀스", required = true) @Positive int commentId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId
    ) throws Exception {
        dailyService.removeDailyComment(dailyId, memberId, commentId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @Operation(summary = "daily like", description = "하루 식단 게시글 좋아요")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/daily/like/{dailyId}/{memberId}")
    public ResponseEntity<String> dailyLike(
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true)@Positive  int dailyId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive  int memberId
    ) throws Exception {
        dailyService.addDailyLike(memberId, dailyId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @Operation(summary = "daily scrap", description = "하루 식단 게시글 스크랩")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/daily/scrap/{dailyId}/{memberId}")
    public ResponseEntity<String> dailyScrap(
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int dailyId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId
    ) throws Exception {
        dailyService.addDailyScrap(dailyId, memberId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @Operation(summary = "daily scrap remove", description = "하루 식단 게시글 스크랩 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @DeleteMapping("/daily/scrap/{dailyId}/{memberId}/{scrapId}")
    public ResponseEntity<String> dailyScrapRemove(
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) @Positive int dailyId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) @Positive int memberId,
        @PathVariable @ApiParam(value = "게시글 스크랩 시퀀스", required = true) @Positive int scrapId
    ) throws Exception {
        dailyService.removeDailyScrap(dailyId, memberId, scrapId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
