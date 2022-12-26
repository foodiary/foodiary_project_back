package com.foodiary.auth.controller;

import com.foodiary.auth.dto.TokenResponse;
import com.foodiary.auth.service.GoogleOauth;
import com.foodiary.auth.service.OAuthService;
import com.foodiary.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Controller
@Slf4j
public class AuthController {

    private final UserService userService;

    @Autowired
    private GoogleOauth googleOauth;

    @GetMapping("/oauth/{provider-id}/callback")
    public ResponseEntity<?> oauthLogin(@PathVariable("provider-id") String providerId, String code) throws Exception {
        log.info(code);
        log.info(providerId);
        String provider = providerId.toUpperCase();
        ResponseEntity<?> response = userService.oauthLogin(provider, code);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 1. 프론트 쪽에서 구글 서버에 로그인해서 코드 발급
    // 2. 발급받은 코드로
}
