package com.foodiary.config.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.foodiary.auth.authexception.CustomAuthenticationEntryPoint;
import com.foodiary.auth.filter.JwtAuthenticationFilter;
import com.foodiary.auth.jwt.JwtProvider;
import com.foodiary.auth.jwt.MemberDetailsService;

import lombok.RequiredArgsConstructor;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final MemberDetailsService memberDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/resources/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .headers().frameOptions().sameOrigin()
                .and()

                .cors(withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .csrf().disable()
                .authorizeRequests()
                    .requestMatchers(request -> CorsUtils.isPreFlightRequest(request)).permitAll()
                
                
                // .antMatchers(
                // "/",
                // "/v3/api-docs/**",
                // "/swagger-ui/**",
                // "/swagger-resources/**",
                // // 여기부터는 스웨거 테스트용, permitAll에 오면 안됨
                // "/member/{memberId}",
                // "/member",
                // "/member/logout",
                // "/member/search/{memberId}",
                // "/member/reissue",
                // "/member/scrap",
                // "/member/scrap/daily/{scrapId}/{memberId}",
                // "/member/scrap/recipe/{scrapId}/{memberId}",
                // "/member/food/{memberId}",
                // "/member/food/list/{memberId}",
                // "/daily",
                // "/daily/{dailyId}/{memberId}",
                // "/daily/comment",
                // "/daily/comment/{dailyId}/{memberId}",
                // "/daily/comment/{dailyId}/{commentId}/{memberId}",
                // "/daily/like/{dailyId}/{memberId}",
                // "/daily/like/{dailyId}/{dailyLikeId}/{memberId}",
                // "/daily/scrap/{dailyId}/{memberId}",

                // "/recipe",
                // "/recipe/{recipeId}/{memberId}",
                // "/recipe/comment",
                // "/recipe/comment/{recipeId}/{memberId}",
                // "/recipe/comment/{recipeId}/{commentId}/{memberId}",
                // "/recipe/like/{recipeId}/{memberId}",
                // "/recipe/like/{recipeId}/{recipeLikeId}/{memberId}",
                // "/recipe/scrap/{recipeId}/{memberId}",
                
                // 권한 필요 url
                // "/member/password/{memberId}",
                // "/member/password/change/jwt",
                // "/member/{memberId}",
                // "/member/post/daily/{memberId}",
                // "/member/post/recipe/{memberId}",
                // "/member/image/{memberId}",
                // "/member/scrap/daily/{memberId}",
                // "/member/scrap/recipe/{memberId}",
                // "/member/like/daily/{memberId}",
                // "/member/like/recipe/{memberId}",
                // "/member/comment/daily/{memberId}",
                // "/member/comment/recipe/{memberId}",
                // "/member/comment/daily/{memberId}/{dailyId}/{dailyCommentId}",
                // "/member/comment/recipe/{memberId}/{recipeId}/{recipeCommentId}",
                // "/question/{memberId}",
                // "/question/{memberId}/{questionId}",
                // "/member/food/{memberId}",
                // "/member/food/{memberId}/{memberFoodId}",


                // 권한 필요x url
                // "/member/find/password",
                // "/member/find/id",
                // "/member/check/loginid",
                // "/member/check/nickname",
                // "/member/check/email",
                // "/member/email/send",
                // "/member/email/send/confirm",
                // "/member/signup",
                // "/notice",
                // "/notice/{noticeId}",
                // "/faq",


                // ).permitAll()
                
                .antMatchers("/dailys/**", "/recipes/**", "/food/**", "/auth/login", "/oauth/**").permitAll()
                
                .anyRequest().authenticated() 
                .and()

                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()

                .formLogin().disable()
                .httpBasic().disable()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(this.jwtProvider, this.memberDetailsService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Refresh"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 출처에 대해 HTTP 통신을 허용
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 모든 출처에 대해 HTTP 통신을 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS")); //파라미터로 지정한 HTTP Method에 대한 HTTP 통신을 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 URL 앞에서 구성한 CORS 정책을 적용
        return source;
    }
    
}

