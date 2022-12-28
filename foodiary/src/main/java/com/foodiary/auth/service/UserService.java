package com.foodiary.auth.service;

import com.foodiary.auth.jwt.JwtProvider;
import com.foodiary.auth.model.*;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.mapper.MemberMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.transform.OutputKeys;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {


    private final OAuthService oAuthService;
    private final JwtProvider jwtProvider;
    private final MemberMapper memberMapper;


    public ResponseEntity<?> oauthLogin(String providerId, String code) throws Exception {
        ResponseEntity<String> accessTokenResponse = oAuthService.createPostRequest(providerId, code);
        log.info(accessTokenResponse.getBody());
        OAuthTokenDto oAuthToken = oAuthService.getAccessToken(accessTokenResponse);
        log.info("Access Token: {}", oAuthToken.getAccessToken());

        ResponseEntity<String> userInfoResponse = oAuthService.createGetRequest(oAuthToken, providerId);
        log.info(userInfoResponse.getBody());

        if(providerId.equals("GOOGLE")){
            GoogleUserDto googleUser = oAuthService.getGoogleUserInfo(userInfoResponse);
            // 신규회원인지 판별
            if (memberMapper.findByEmail(googleUser.email) == null){
                NewUserResponseDto response = new NewUserResponseDto(googleUser.email, googleUser.picture, true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else{
                TokenResponse response = createTokenResponse(googleUser.email);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        else if (providerId.equals("NAVER")) {
            NaverUserDto naverUser = oAuthService.getNaverUserInfo(userInfoResponse);
            // 신규회원인지 판별
            if (memberMapper.findByEmail(naverUser.email) == null){
                NewUserResponseDto response = new NewUserResponseDto(naverUser.email, naverUser.profile_image, true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else{
                TokenResponse response = createTokenResponse(naverUser.email);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return null;
    }
    private TokenResponse createTokenResponse(String email) throws Exception {
        MemberDto member = memberMapper.findByEmail(email);
        log.info(member.getMemberEmail());
        TokenResponse tokenResponse = jwtProvider.createTokensByLogin(email);
        tokenResponse.setAccessTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60));
        tokenResponse.setRefreshTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60 * 24 * 7));
        return tokenResponse;
    }

    public TokenResponse createLoginTokenResponse(String email, String pw) throws Exception {
        MemberDto member = memberMapper.findByEmailAndPw(email, pw);
        log.info(member.getMemberEmail());
        TokenResponse tokenResponse = jwtProvider.createTokensByLogin(email);
        tokenResponse.setAccessTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60));
        tokenResponse.setRefreshTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60 * 24 * 7));
        return tokenResponse;
    }

    public Claims oauthVerify(String jwt) throws Exception {
        return jwtProvider.verifyToken(jwt);
    }


    private boolean isJoinedUser(GoogleUserDto googleUser) {
        MemberDto member = memberMapper.findByEmail(googleUser.getEmail());
        log.info("Joined User: {}", member);
        return member == null;
    }

}
