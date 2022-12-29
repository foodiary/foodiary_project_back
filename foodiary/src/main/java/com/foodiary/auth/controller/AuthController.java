package com.foodiary.auth.controller;

import com.foodiary.auth.model.TokenResponseDto;
import com.foodiary.auth.service.GoogleOauth;
import com.foodiary.auth.service.UserService;
import com.foodiary.config.security.SecurityConfig;
import com.foodiary.member.model.MemberLoginRequestDto;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@Slf4j
public class AuthController {

    private final UserService userService;


    @Operation(summary = "oauth login", description = "oauth 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @GetMapping("/oauth/{provider-id}/callback")
    public ResponseEntity<?> oauthLogin(@PathVariable("provider-id") @ApiParam(value = "provider") String providerId,
                                        @RequestParam @ApiParam(value = "인가 코드") String code) throws Exception {
        log.info(code);
        log.info(providerId);
        String provider = providerId.toUpperCase();
        ResponseEntity<?> response = userService.oauthLogin(provider, code);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
//


    @Operation(summary = "service login", description = "자체 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @PostMapping("/login")
    public ResponseEntity<?> memberLogin(@RequestBody MemberLoginRequestDto loginDto) throws Exception {
        TokenResponseDto response = userService.createLoginTokenResponse(loginDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
