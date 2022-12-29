package com.foodiary.auth.service;

import com.foodiary.auth.jwt.JwtProvider;
import com.foodiary.auth.model.*;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberLoginRequestDto;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
                TokenResponseDto response = createTokenResponse(googleUser.email);
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
                TokenResponseDto response = createTokenResponse(naverUser.email);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return null;
    }
    private TokenResponseDto createTokenResponse(String email) throws Exception {
        MemberDto member = memberMapper.findByEmail(email);
        log.info(member.getMemberEmail());
        TokenResponseDto tokenResponseDto = jwtProvider.createTokensByLogin(email);
        tokenResponseDto.setAccessTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60));
        tokenResponseDto.setRefreshTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60 * 24 * 7));
        return tokenResponseDto;
    }

    public TokenResponseDto createLoginTokenResponse(MemberLoginRequestDto loginDto) throws Exception {
        MemberDto member = memberMapper.findByEmailAndPw(loginDto.getLoginId(), loginDto.getPassword());
        log.info(member.getMemberEmail());
        TokenResponseDto tokenResponseDto = jwtProvider.createTokensByLogin(loginDto.getLoginId());
        tokenResponseDto.setAccessTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60));
        tokenResponseDto.setRefreshTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60 * 24 * 7));
        return tokenResponseDto;
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
