package com.foodiary.search.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodiary.search.model.SearchResponseMemberDto;
import com.foodiary.search.model.SearchRequestDto;
import com.foodiary.search.model.SearchRequestMemberDto;
import com.foodiary.search.model.SearchResponseDto;
import com.foodiary.search.service.SearchService;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class SearchController {
    
    private final SearchService searchService;

    @Operation(summary = "search keyword delete", description = "최근 검색어 삭제하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @DeleteMapping(value = "/search/delete/{memberId}/{keywordId}")
    public ResponseEntity<String> searchDelete(
        @PathVariable @ApiParam(value = "회원 시퀀스")int memberId,
        @PathVariable @ApiParam(value = "키워드 시퀀스")int keywordId
    ) throws Exception {

        searchService.delete(memberId, keywordId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Operation(summary = "search result", description = "검색 하기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/search/result")
    public ResponseEntity<List<SearchResponseDto>> searchFind(
        @RequestBody SearchRequestDto searchRequestDto
    ) throws Exception {

        List<SearchResponseDto> searchResponseDto = searchService.search(searchRequestDto);
        return new ResponseEntity<>(searchResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "search view", description = "검색 보기")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @PostMapping(value = "/search")
    public ResponseEntity<List<SearchResponseMemberDto>> searchs(
        @RequestBody SearchRequestMemberDto sRequestMemberDto
    ) throws Exception {

        List<SearchResponseMemberDto> searchMemberDtoList = searchService.searchView(sRequestMemberDto);

        return new ResponseEntity<>(searchMemberDtoList, HttpStatus.OK);
    }
    
}
