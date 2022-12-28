package com.foodiary.auth.jwt;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.foodiary.auth.model.TokenResponse;
import com.foodiary.member.model.MemberDto;
import com.foodiary.redis.RedisDao;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    
    private final RedisDao redisDao;

     @Value("${jwt.secret-key}")
    private String secretKey;

     @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

     @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

//    public String encodeBase64SecretKey(String secretKey) {
//        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
//    }

    private Key createKey() {
        // signiture에 대한 정보는 Byte array로 구성되어있습니다.
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        return signingKey;
    }

    public Date getTokenExpiration(int expirationMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationMinutes);
        Date expiration = calendar.getTime();

        return expiration;
    }

    public TokenResponse createTokensByLogin(String email) throws Exception {

        String atk = "Bearer " + delegateAccessToken(email);
        String rtk = delegateRefreshToken(email);
        redisDao.setValues(email, rtk, Duration.ofMinutes((long) refreshTokenExpirationMinutes));
        return new TokenResponse(atk, rtk, "bearer", false);
    }


    public String delegateAccessToken(String email) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("memberId", email);

        String subject = email;
        Date expiration = getTokenExpiration(accessTokenExpirationMinutes);

        return generateAccessToken(claims, subject, expiration);
    }

    public String delegateRefreshToken(String email) {
        String subject = email;
        Date expiration = getTokenExpiration(refreshTokenExpirationMinutes);

        return generateRefreshToken(subject, expiration);
    }
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;


    public String generateAccessToken(Map<String, Object> claims,
                                      String subject,
                                      Date expiration) throws Exception{

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(createKey(), signatureAlgorithm)
                .compact();
    }

    public String generateRefreshToken(String subject,
                                       Date expiration) {


        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(createKey(), signatureAlgorithm)
                .compact();
    }

    public String getSubject(String jws) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .build()
                .parseClaimsJws(jws)
                .getBody();
        System.out.println(claims.toString());
        return (String) claims.get("email");
    }



    public Jws<Claims> getClaims(String jws) {

        return Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .build()
                .parseClaimsJws(jws);

    }

    //엑세스 토큰 검증하는 로직
    public Claims verifyToken(String jws){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                    .build()
                    .parseClaimsJws(jws)
                    .getBody();
            log.info("토큰 검증 완료");
            return claims;
        } catch (JwtException e) {
            return null;
        }
    }

    public TokenResponse reissueAtk(MemberDto member) throws Exception {
        String rtkInRedis = redisDao.getValues(member.getMemberEmail());
        if (Objects.isNull(rtkInRedis)) {
            throw new JwtException("인증 정보가 만료되었습니다.");
        }

        String atk = delegateAccessToken(member.getMemberEmail());
        return new TokenResponse(atk, null, "bearer", false);
    }

    public void deleteRtk(MemberDto member) throws JwtException {
        redisDao.deleteValues(member.getMemberEmail());
    }

    public void setBlackListAtk(String bearerAtk) {
        String atk = bearerAtk.substring(7);
        long expiration = getClaims(atk).getBody().getExpiration().getTime();
        long now = Calendar.getInstance().getTime().getTime();

        redisDao.setValues(atk, "logout", Duration.ofMillis(expiration-now));
    }

    public boolean isBlackList(String atk) {
        return StringUtils.hasText(redisDao.getValues(atk));
    }
}
