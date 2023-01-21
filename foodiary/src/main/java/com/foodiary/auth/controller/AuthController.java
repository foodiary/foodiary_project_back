package com.foodiary.auth.controller;

import com.foodiary.auth.jwt.CustomUserDetails;
import com.foodiary.auth.model.TokenReissueDto;
import com.foodiary.auth.model.TokenResponseDto;
import com.foodiary.auth.service.UserService;
import com.foodiary.member.model.MemberLoginRequestDto;

import io.jsonwebtoken.JwtException;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

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
        return response;
    }
//


    @Operation(summary = "service login", description = "자체 로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @PostMapping("auth/login")
    public ResponseEntity<?> memberLogin(@Valid @RequestBody MemberLoginRequestDto loginDto) throws Exception {
        TokenResponseDto response = userService.createLoginTokenResponse(loginDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("auth/logout")
    public ResponseEntity logout(@AuthenticationPrincipal CustomUserDetails memberDetails,
                                 @RequestHeader("Authorization") String bearerAtk) throws JwtException {
        userService.memberLogout(memberDetails, bearerAtk);

        return new ResponseEntity<>("로그아웃 되었습니다.", HttpStatus.OK);
    }



    @GetMapping("/auth/reissue")
    public ResponseEntity reissue(@RequestHeader("Refresh") String refreshToken) throws Exception {
        TokenReissueDto response = userService.tokenReissue(refreshToken);
        log.info("토큰 재발급에 성공하였습니다.");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/auth/time")
    public ResponseEntity verifyTime() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        return new ResponseEntity<>(now, HttpStatus.OK);
    }


}
