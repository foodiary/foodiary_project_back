package com.foodiary.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOauth {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;


    public static String GOOGLE_LOGIN_URL;

    @Value("${google.client_id}")
    public String GOOGLE_CLIENT_ID;

    @Value("${google.redirect_uri}")
    public String GOOGLE_CALLBACK_URL;

    @Value("${google.client_secret}")
    public String GOOGLE_CLIENT_SECRET;

    @Value("${google.grant_type}")
    public String GOOGLE_DATA_ACCESS_SCOPE;


}
