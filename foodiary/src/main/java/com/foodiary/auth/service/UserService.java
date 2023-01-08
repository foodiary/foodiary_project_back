package com.foodiary.auth.service;

import java.security.MessageDigest;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.foodiary.auth.jwt.CustomUserDetails;
import com.foodiary.auth.jwt.JwtProvider;
import com.foodiary.auth.model.GoogleUserDto;
import com.foodiary.auth.model.NaverUserDto;
import com.foodiary.auth.model.NewUserResponseDto;
import com.foodiary.auth.model.OAuthTokenDto;
import com.foodiary.auth.model.TokenResponseDto;
import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberLoginRequestDto;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        MemberDto member = verifyMember(email);
        log.info(member.getMemberEmail());
        TokenResponseDto tokenResponseDto = jwtProvider.createTokensByLogin(member);
        tokenResponseDto.setAccessTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60));
        tokenResponseDto.setRefreshTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60 * 24 * 7));
        return tokenResponseDto;
    }

    public TokenResponseDto createLoginTokenResponse(MemberLoginRequestDto loginDto) throws Exception {
        MemberDto member = memberMapper.findByLoginIdAndPw(loginDto.getLoginId(), loginDto.getPassword()).orElseThrow();
        log.info(member.getMemberEmail());
        TokenResponseDto tokenResponseDto = jwtProvider.createTokensByLogin(member);
        tokenResponseDto.setAccessTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60));
        tokenResponseDto.setRefreshTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60 * 24 * 7));
        return tokenResponseDto;
    }

    public Claims oauthVerify(String jwt) throws Exception {
        return jwtProvider.verifyToken(jwt);
    }


    private boolean isJoinedUser(GoogleUserDto googleUser) {
        MemberDto member = verifyMember(googleUser.email);
        log.info("Joined User: {}", member);
        return member == null;
    }

    private MemberDto verifyMember(String email) {
        return memberMapper.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public boolean checkUser(int memberId) {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("userId : {}", principal.getUsername());
        int id = Integer.parseInt(principal.getUsername());
        if(memberId != id) {
            throw new BusinessLogicException(ExceptionCode.NOT_AUTHORIZED);
        } else return true;
    }

    public boolean verifyUser(int memberId) {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("userId : {}", principal.getUsername());
        int id = Integer.parseInt(principal.getUsername());
        if(memberId == id) {
            return true;
        } else return false;
    }

    public void verifySave(int saveCheck) {
        if(saveCheck < 1) {
            throw new BusinessLogicException(ExceptionCode.SAVE_ERROR);
        }
    }

    public void verifyUpdate(int updateCheck) {
        if(updateCheck < 1) {
            throw new BusinessLogicException(ExceptionCode.UPDATE_ERROR);
        }
    }

    public void verifyDelete(int deleteCheck) {
        if(deleteCheck < 1) {
            throw new BusinessLogicException(ExceptionCode.DELETE_ERROR);
        }
    }

    public String encrypt(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] passBytes = s.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digested.length; i++)
                sb.append(Integer.toString((digested[i] & 0xff) + 0x100, 16).substring(1));
            return sb.toString();
        } catch (Exception e) {
            return s;
        }
    }

}
