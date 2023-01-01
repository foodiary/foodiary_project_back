package com.foodiary.search.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodiary.search.model.SearchRequestDto;
import com.foodiary.search.model.SearchRequestMemberDto;
import com.foodiary.search.model.SearchResponseDto;
import com.foodiary.search.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class SearchController {
    
    private final SearchService searchService;

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
    public ResponseEntity<List<String>> searchs(
        @RequestBody SearchRequestMemberDto sRequestMemberDto
    ) throws Exception {

        List<String> searchList = searchService.searchView(sRequestMemberDto);
        return new ResponseEntity<>(searchList, HttpStatus.OK);
    }
    
}
