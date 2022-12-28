package com.foodiary.auth.controller;

import com.foodiary.auth.model.TokenResponse;
import com.foodiary.auth.service.GoogleOauth;
import com.foodiary.auth.service.OAuthService;
import com.foodiary.auth.service.UserService;
import com.foodiary.member.model.MemberLoginDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.apache.el.parser.Token;
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

    @Autowired
    private GoogleOauth googleOauth;

    @GetMapping("/oauth/{provider-id}/callback")
    public ResponseEntity<?> oauthLogin(@PathVariable("provider-id") String providerId, @RequestParam String code) throws Exception {
        log.info(code);
        log.info(providerId);
        String provider = providerId.toUpperCase();
        ResponseEntity<?> response = userService.oauthLogin(provider, code);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> memberLogin(@RequestParam MemberLoginDto loginDto) throws Exception {
        TokenResponse response = userService.createLoginTokenResponse(loginDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
