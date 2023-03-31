package com.foodiary.rank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.rank.model.RanksResponseDto;
import com.foodiary.rank.service.RankService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class RankController {
    
    private final RankService rankService;

    @Operation(summary = "ranking", description = "랭크 페이지 1주일 (좋아요 많은 게시글 TOP 20)")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/rank/week")
    public ResponseEntity<List<RanksResponseDto>> ranksWeek() throws Exception {

        List<RanksResponseDto> ranksResponseDtoList =rankService.rankWeekView();

        if(ranksResponseDtoList.size()==0) {
            throw new BusinessLogicException(ExceptionCode.RANK_NOT_FOUND);
        }

        return new ResponseEntity<>(ranksResponseDtoList, HttpStatus.OK);
    }

    @Operation(summary = "ranking", description = "랭크 페이지 한달 (좋아요 많은 게시글 TOP 20)")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/rank/month")
    public ResponseEntity<List<RanksResponseDto>> ranksMonth() throws Exception {

        List<RanksResponseDto> ranksResponseDtoList =rankService.rankMonthView();

        if(ranksResponseDtoList.size()==0) {
            throw new BusinessLogicException(ExceptionCode.RANK_NOT_FOUND);
        }
        
        return new ResponseEntity<>(ranksResponseDtoList, HttpStatus.OK);
    }

    @Operation(summary = "ranking", description = "랜덤 게시글 추천")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/rank/recommend")
    public ResponseEntity<List<RanksResponseDto>> ranksRecommend() throws Exception {

        List<RanksResponseDto> ranksResponseDtoList =rankService.rankRecommendView();

        if(ranksResponseDtoList.size()==0) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
        
        return new ResponseEntity<>(ranksResponseDtoList, HttpStatus.OK);
    }
}
