package com.foodiary.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/resources/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(
                "/",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-resources/**",
                // 여기부터는 스웨거 테스트용, permitAll에 오면 안됨
                "/member/{memberId}",
                "/member",
                "/member/logout",
                "/member/search/{memberId}",
                "/member/reissue",
                "/member/scrap",
                "/member/scrap/daily/{scrapId}/{memberId}",
                "/member/scrap/recipe/{scrapId}/{memberId}",
                "/member/food/{memberId}",
                "/member/food/list/{memberId}",
                "/daily",
                "/daily/{dailyId}/{memberId}",
                "/daily/comment",
                "/daily/comment/{dailyId}/{dailyCommentId}/{memberId}",
                "/daily/like/{dailyId}/{memberId}",
                "/daily/like/{dailyId}/{dailyLikeId}/{memberId}",
                "/daily/scrap/{dailyId}/{memberId}",
                
                // 권한 필요없는 url
                "/member/login",
                "/member/signup",
                "/member/search",
                "/dailys",
                "/daily/details"



                ).permitAll()
                .anyRequest().authenticated() 
                .and()
                .exceptionHandling() 
                .and()
                .logout().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .build();
    }
}

