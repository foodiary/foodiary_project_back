package com.foodiary.auth.service;

import com.foodiary.auth.jwt.CustomUserDetails;
import com.foodiary.auth.jwt.JwtProvider;
import com.foodiary.auth.model.*;
import com.foodiary.common.exception.BusinessLogicException;
import com.foodiary.common.exception.ExceptionCode;
import com.foodiary.member.mapper.MemberMapper;
import com.foodiary.member.model.MemberDto;
import com.foodiary.member.model.MemberLoginRequestDto;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final OAuthService oAuthService;
    private final JwtProvider jwtProvider;
    private final MemberMapper memberMapper;
    private final RestTemplate restTemplate;

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
                // 신규 회원일 경우
                NewUserResponseDto response = new NewUserResponseDto(googleUser.email, googleUser.picture, true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else{
                // 기존 회원일 경우
                TokenResponseDto response = createTokenResponse(googleUser.email);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        else if (providerId.equals("NAVER")) {
            NaverUserDto naverUser = oAuthService.getNaverUserInfo(userInfoResponse);
            // 신규회원인지 판별
            if (memberMapper.findByEmail(naverUser.email) == null){
                // 신규 회원일 경우
                NewUserResponseDto response = new NewUserResponseDto(naverUser.email, naverUser.profile_image, true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else{
                // 기존 회원일 경우
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
        String encryptPw = encrypt(loginDto.getPassword());
        log.info(encryptPw);
        MemberDto member = memberMapper.findByLoginIdAndPw(loginDto.getLoginId(), encryptPw)
                        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ID_PW_BAD_REQUEST));

        log.info(member.getMemberEmail());
        log.info(member.getMemberPassword());

        TokenResponseDto tokenResponseDto = jwtProvider.createTokensByLogin(member);
        tokenResponseDto.setAccessTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60));
        tokenResponseDto.setRefreshTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60 * 24 * 7));
        return tokenResponseDto;
    }

    public TokenReissueDto tokenReissue(String refreshToken) throws Exception {
        String memberEmail = jwtProvider.getClaims(refreshToken).getBody().getSubject();
        log.info(memberEmail);
        MemberDto member = memberMapper.findByEmail(memberEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        TokenResponseDto tokenResponse = jwtProvider.reissueAtk(member, refreshToken);

        return TokenReissueDto.builder()
                    .memberId(member.getMemberId())
                    .accessToken(tokenResponse.getAccessToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .accessTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60))
                    .refreshTokenExpirationMinutes(LocalDateTime.now().plusMinutes(60 * 24 * 14))
                    .build();
    }

    public void memberLogout(CustomUserDetails memberDetails, String atk) {
        MemberDto member = memberDetails.getMember();

        jwtProvider.setBlackListAtk(atk);
        jwtProvider.deleteRtk(member);
    }

    public Claims oauthVerify(String jwt) throws Exception {
        return jwtProvider.verifyToken(jwt);
    }


    private boolean isJoinedUser(GoogleUserDto googleUser) {
        MemberDto member = verifyMember(googleUser.getEmail());
        log.info("Joined User: {}", member);
        return member == null;
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
}
