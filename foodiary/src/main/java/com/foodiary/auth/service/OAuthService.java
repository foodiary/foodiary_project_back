package com.foodiary.auth.service;

//import com.foodiary.auth.authexception.CustomAuthException;
import com.foodiary.auth.common.CommonCode;
import com.foodiary.auth.dto.NaverUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.foodiary.auth.dto.GoogleUser;
import com.foodiary.auth.dto.OAuthToken;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Service
@Slf4j
public class OAuthService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    @Autowired private GoogleOauth googleOauth;
    @Autowired private NaverOauth naverOauth;


    public OAuthService(RestTemplate restTemplate) {
        this.objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> createPostRequest(String providerId, String code) {

        log.debug(code);
        String googleUrl = "https://oauth2.googleapis.com/token";
        String naverUrl = "https://nid.naver.com/oauth2.0/token";

        switch (providerId){
            case "GOOGLE":
                MultiValueMap<String, String> googleParams = new LinkedMultiValueMap<>();
                googleParams.add("code", code);
                googleParams.add("client_id", googleOauth.GOOGLE_CLIENT_ID);
                googleParams.add("client_secret", googleOauth.GOOGLE_CLIENT_SECRET);
                googleParams.add("redirect_uri", googleOauth.GOOGLE_CALLBACK_URL);
                googleParams.add("grant_type", googleOauth.GOOGLE_DATA_ACCESS_SCOPE);

                HttpHeaders googleHeaders = new HttpHeaders();
                googleHeaders.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");


                HttpEntity<MultiValueMap<String, String>> googleHttpEntity = new HttpEntity<>(googleParams, googleHeaders);
                return restTemplate.exchange(googleUrl, HttpMethod.POST, googleHttpEntity, String.class);

            case "NAVER":
                MultiValueMap<String, String> naverParams = new LinkedMultiValueMap<>();
                naverParams.add("code", code);
                naverParams.add("client_id", naverOauth.NAVER_CLIENT_ID);
                naverParams.add("client_secret", naverOauth.NAVER_CLIENT_SECRET);
                naverParams.add("redirect_uri", naverOauth.NAVER_CALLBACK_URL);
                naverParams.add("grant_type", naverOauth.NAVER_DATA_ACCESS_SCOPE);

                HttpHeaders naverHeaders = new HttpHeaders();
                naverHeaders.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");


                HttpEntity<MultiValueMap<String, String>> naverHttpEntity = new HttpEntity<>(naverParams, naverHeaders);
                return restTemplate.exchange(naverUrl, HttpMethod.POST, naverHttpEntity, String.class);

            default: return null;
        }
    }

    public OAuthToken    getAccessToken(ResponseEntity<String> response) {
        OAuthToken oAuthToken = null;
        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
            } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return oAuthToken;
    }

    public ResponseEntity<String> createGetRequest(OAuthToken oAuthToken, String providerId) {
        String googleUrl = "https://www.googleapis.com/oauth2/v1/userinfo";
        String naverUrl = "https://openapi.naver.com/v1/nid/me";
        String url = null;

        if (providerId.equals("GOOGLE")) url = googleUrl;
        else if (providerId.equals("NAVER")) url = naverUrl;
        else throw new RuntimeException("providerId를 다시 입력해주세요");

        log.info(url);
        log.info(oAuthToken.getAccessToken());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oAuthToken.getAccessToken());
        log.info(headers.get("Authorization").get(0));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);

        return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }



    public GoogleUser getGoogleUserInfo(ResponseEntity<String> userInfoResponse) {
        GoogleUser googleUser = null;
        try {
            googleUser = objectMapper.readValue(userInfoResponse.getBody(), GoogleUser.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return googleUser;
    }

    public NaverUser getNaverUserInfo(ResponseEntity<String> userInfoResponse) {
        NaverUser naverUser = null;
        try {
            String first = userInfoResponse.getBody().substring(50);
            String request = first.substring(0, first.length()-1);
            log.info(request);
            naverUser = objectMapper.readValue(request, NaverUser.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return naverUser;
    }
}
