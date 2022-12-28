package com.foodiary.auth.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.foodiary.auth.jwt.JwtProvider;
import com.foodiary.auth.jwt.MemberDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class JwtAuthenticationFilter extends OncePerRequestFilter{
    private final JwtProvider jwtProvider;
    private final MemberDetailsService memberDetailsService;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, MemberDetailsService memberDetailsService) {
        this.jwtProvider = jwtProvider;
        this.memberDetailsService = memberDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String access = request.getHeader("Authorization");
        String refresh = request.getHeader("Refresh");
        String requestURI = request.getRequestURI();

        if ((!Objects.isNull(access) && access.startsWith("Bearer ")) || requestURI.equals("/members/reissue")) {
            try {

                // 요청이 엑세스 토큰 재발급 요청이면 if문 실행 (재발급)
                if (requestURI.equals("/members/reissue")) {
                    String rtkSubject = jwtProvider.getClaims(refresh).getBody().getSubject();
                    UserDetails userDetails = memberDetailsService.loadUserByUsername(rtkSubject);
                    Authentication token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
                // 재발급 요청이 아닌 경우 access 토큰 유효성 검증
                else {
                    String atk = access.substring(7);

                    Claims claims = jwtProvider.verifyToken(atk);
                    if(claims == null){
                        throw new JwtException("유효하지 않은 Token 입니다.");
                    }
                    if (jwtProvider.isBlackList(atk)) {
                        throw new JwtException("AccessToken이 만료되었습니다.");
                    }
                    String atkSubject = (String) claims.get("email");
                    System.out.println(atkSubject);


                    UserDetails userDetails = memberDetailsService.loadUserByUsername(atkSubject);
                    Authentication token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            } catch (JwtException e) {
                request.setAttribute("exception", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
