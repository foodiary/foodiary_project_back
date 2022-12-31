package com.foodiary.rank.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.foodiary.common.exception.VaildErrorResponseDto;
import com.foodiary.member.model.MemberSignUpRequestDto;
import com.foodiary.rank.service.RankService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class RankController {
    
    private final RankService rankService;

    @Operation(summary = "ranking", description = "랭크 페이지 (좋아요 많은 게시글 TOP 20)")
    @ApiResponses({ 
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @ResponseBody
    @GetMapping(value = "/rank")
    public ResponseEntity<?> ranks() throws Exception {


        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
