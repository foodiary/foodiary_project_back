package com.foodiary.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class NaverOauth {


    public static String NAVER_LOGIN_URL;

    @Value("${naver.client_id}")
    public String NAVER_CLIENT_ID;

    @Value("${naver.redirect_uri}")
    public String NAVER_CALLBACK_URL;

    @Value("${naver.client_secret}")
    public String NAVER_CLIENT_SECRET;

    @Value("${naver.grant_type}")
    public String NAVER_DATA_ACCESS_SCOPE;

    @Value("${naver.state}")
    public String NAVER_STATE;
}
