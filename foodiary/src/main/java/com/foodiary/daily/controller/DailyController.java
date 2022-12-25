package com.foodiary.daily.controller;

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

import com.foodiary.daily.model.DailyCommentDetailsDto;
import com.foodiary.daily.model.DailyDetailsDto;
import com.foodiary.daily.model.DailysDto;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Controller
public class DailyController {
    
    // TODO : 하루식단 게시글 쓸때 무조건 이미지 첨부할건지? 아니면 그냥 줄글 가능하게 할건지?
    @Operation(summary = "daily write", description = "하루 식단 게시글 작성")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @PostMapping(value = "/daily")
    public ResponseEntity<String> dailyWrite(
        @Parameter(description="회원 시퀀스", example = "3498", required = true)
        @RequestPart("memberId") String memberId, // int로 안받아져서 string으로 받음
        @Parameter(description="게시글 제목", example = "제목입니다", required = true)
        @RequestPart("title") String title,
        @Parameter(description="게시글 내용", example = "내용입니다", required = true)
        @RequestPart("content") String content,
        @Parameter(description = "사진 이미지")
        @RequestPart(value = "memberImage", required = true) MultipartFile memberImage
    ) throws Exception {

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "daily modify", description = "하루 식단 게시글 수정")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @PatchMapping(value = "/daily/{dailyId}/{memberId}")
    public ResponseEntity<String> dailyModify(
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int dailyId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true)int memberId,
        @Parameter(description="게시글 제목", example = "제목입니다")
        @RequestPart(value = "title", required = false) String title,
        @Parameter(description="게시글 내용", example = "내용입니다")
        @RequestPart(value = "content", required = false) String content,
        @Parameter(description = "사진 이미지")
        @RequestPart(value = "memberImage", required = false) MultipartFile memberImage
    ) throws Exception {

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "daily list", description = "하루 식단 게시판 보기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/dailys")
    public ResponseEntity<List<DailysDto>> dailys(
        @ApiParam(value = "게시판 페이지", required = false) int pageNum
    ) throws Exception {

        DailysDto dailysDto = new DailysDto(1, "제목입니다.", "경로입니다.", 1, 2, LocalDateTime.now(), 5);
        List<DailysDto> dailyList = new ArrayList<>();

        dailyList.add(dailysDto);

        return new ResponseEntity<>(dailyList, HttpStatus.OK);
    }

    @Operation(summary = "daily list", description = "하루 식단 게시글 상세 보기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/daily/details")
    public ResponseEntity<List<DailyDetailsDto>> dailyDefails(
        @ApiParam(value = "게시글 시퀀스", required = true) int dailyId,
        @ApiParam(value = "게시판 페이지", required = false) int pageNum
    ) throws Exception {

        DailyCommentDetailsDto dailyCommentDto = new DailyCommentDetailsDto(1, dailyId, 1, "댓글 작성자", "댓글 내용입니다.");

        List<DailyCommentDetailsDto> dailyCommentDtoList = new ArrayList<>();

        dailyCommentDtoList.add(dailyCommentDto);
        
        DailyDetailsDto dailyDto = new DailyDetailsDto(dailyId, 1, "제목입니다.", "내용입니다", "경로입니다", 5, 7, LocalDateTime.now(), 5, dailyCommentDtoList);

        List<DailyDetailsDto> dailyDetailsDtoList = new ArrayList<>();

        dailyDetailsDtoList.add(dailyDto);

        return new ResponseEntity<>(dailyDetailsDtoList, HttpStatus.OK);
    }

    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
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
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int dailyId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
    ) throws Exception {

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @Operation(summary = "daily comment modify", description = "하루 식단 게시글 댓글 작성")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/daily/comment/{dailyId}/{memberId}")
    public ResponseEntity<String> dailyCommentWrite(
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int dailyId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId,
        @RequestBody @ApiParam(value = "댓글 내용", required = true) String content // TODO : 리퀘스트 바디 설명 안나옴, 수정필요
    ) throws Exception {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "daily comment modify", description = "하루 식단 게시글 댓글 조회")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/daily/comment")
    public ResponseEntity<List<DailyCommentDetailsDto>> dailyCommentDetails(
        @ApiParam(value = "게시글 시퀀스", required = true) int dailyId,
        @ApiParam(value = "댓글 페이지", required = true) int pageNum
    ) throws Exception {
        DailyCommentDetailsDto dailyCommentDetailsDto = new DailyCommentDetailsDto(1, dailyId, 1, "댓글 작성자", "댓글 내용입니다.");
        List<DailyCommentDetailsDto> detailsDtos = new ArrayList<>();
        detailsDtos.add(dailyCommentDetailsDto);
        return new ResponseEntity<>(detailsDtos, HttpStatus.OK);
    }

    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @Operation(summary = "daily comment modify", description = "하루 식단 게시글 댓글 수정")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PatchMapping(value = "/daily/comment/{dailyId}/{commentId}/{memberId}")
    public ResponseEntity<String> dailyCommentModify(
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int dailyId,
        @PathVariable @ApiParam(value = "댓글 시퀀스", required = true) int commentId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId,
        @RequestBody String content
    ) throws Exception {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @Operation(summary = "daily comment delete", description = "하루 식단 게시글 댓글 삭제")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @DeleteMapping(value = "/daily/comment/{dailyId}/{commentId}/{memberId}")
    public ResponseEntity<String> dailyCommentDelete(
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int dailyId,
        @PathVariable @ApiParam(value = "댓글 시퀀스", required = true) int commentId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
    ) throws Exception {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
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
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int dailyId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
    ) throws Exception {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @Operation(summary = "daily like cancle", description = "하루 식단 게시글 좋아요 취소")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @DeleteMapping(value = "/daily/like/{dailyId}/{dailyLikeId}/{memberId}")
    public ResponseEntity<String> dailyLikeCancle(
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int dailyId,
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int dailyLikeId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
    ) throws Exception {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ApiImplicitParam(name = "accessToken", value = "JWT Token", required = true, dataType = "string", paramType = "header")
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
        @PathVariable @ApiParam(value = "게시글 시퀀스", required = true) int dailyId,
        @PathVariable @ApiParam(value = "회원 시퀀스", required = true) int memberId
    ) throws Exception {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
