package com.foodiary.search.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodiary.search.model.SearchResponseMemberDto;
import com.foodiary.search.model.SearchRequestDto;
import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.search.model.SearchDailyResponseDto;
import com.foodiary.search.model.SearchRecipeResponseDto;
import com.foodiary.search.service.SearchService;
import com.github.pagehelper.PageHelper;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class SearchController {
    
    private final SearchService searchService;

    @Operation(summary = "search keyword delete", description = "하루식단 최근 검색어 삭제하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @DeleteMapping(value = "/search/daily/delete/{memberId}/{keywordId}")
    public ResponseEntity<String> searchDeleteDaily(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId,
        @PathVariable @ApiParam(value = "키워드 시퀀스")int keywordId
    ) throws Exception {

        searchService.deleteDaily(memberId, keywordId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "search result", description = "하루식단 검색 하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/search/daily/result")
    public ResponseEntity<List<SearchDailyResponseDto>> searchFindDaily(
        @RequestBody @Valid SearchRequestDto searchRequestDto
    ) throws Exception {

        if(searchRequestDto.getPage() <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(searchRequestDto.getPage(), 10);
        List<SearchDailyResponseDto> searchResponseDto = searchService.searchDaily(searchRequestDto);
        return new ResponseEntity<>(searchResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "search view", description = "하루식단 검색어 보기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/search/daily")
    public ResponseEntity<List<SearchResponseMemberDto>> searchsDaily(
        @RequestParam(required = true) @ApiParam(value="멤버 시퀀스", required = true) int memberId
    ) throws Exception {

        List<SearchResponseMemberDto> searchMemberDtoList = searchService.searchViewDaily(memberId);

        return new ResponseEntity<>(searchMemberDtoList, HttpStatus.OK);
    }

    @Operation(summary = "search keyword delete", description = "레시피 최근 검색어 삭제하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @DeleteMapping(value = "/search/recipe/delete/{memberId}/{keywordId}")
    public ResponseEntity<String> searchDeleteRecipe(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId,
        @PathVariable @ApiParam(value = "키워드 시퀀스")int keywordId
    ) throws Exception {

        searchService.deleteRecipe(memberId, keywordId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "search result", description = "레시피 검색 하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/search/recipe/result")
    public ResponseEntity<List<SearchRecipeResponseDto>> searchFind(
        @RequestBody @Valid SearchRequestDto searchRequestDto
    ) throws Exception {

        if(searchRequestDto.getPage() <= 0){
            throw new BusinessLogicException(ExceptionCode.BAD_REQUEST);
        }
        PageHelper.startPage(searchRequestDto.getPage(), 10);
        List<SearchRecipeResponseDto> searchResponseDto = searchService.searchRecipe(searchRequestDto);
        return new ResponseEntity<>(searchResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "search view", description = "레시피 검색어 보기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ApiImplicitParam(name = "Authorization", value = "JWT Token", required = true, dataType = "string", paramType = "header")
    @ResponseBody
    @GetMapping(value = "/search/recipe")
    public ResponseEntity<List<SearchResponseMemberDto>> searchsRecipe(
        @ApiParam(value="멤버 시퀀스", required = true) int memberId
    ) throws Exception {

        List<SearchResponseMemberDto> searchMemberDtoList = searchService.searchViewRecipe(memberId);

        return new ResponseEntity<>(searchMemberDtoList, HttpStatus.OK);
    }
    
}
