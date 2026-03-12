package com.spring.app.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.spring.app.auth.domain.CustomUserDetails;
import com.spring.app.member.domain.MemberDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/*
	Spring Security 와 JSON Web Token 을 사용하여
    인증(Authentication) 및 인가(Authorization)를 처리하기 위해
    JWT의 생성, 서명 검증, Claim 추출, Authentication 객체 변환 기능을 제공하는 클래스이다.
*/

@Component
@Slf4j
public class JwtTokenProvider {

	private static final String AUTHORITIES_KEY = "auth";

	private static final String BEARER_TYPE = "Bearer";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60;
    // 60분. Access Token (액세스 토큰) 유효기간

    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;
    // 7일. Refresh Token (리프레시 토큰) 유효기간

    private final SecretKey secretKey;


    // 생성자
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
    	byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }


    /* 인증된 사용자의 정보(Authentication authentication)를 가지고
       AccessToken 과 RefreshToken 을 생성하여 JWT 토큰을 생성해주는 메서드 */
    public JwtToken generateToken(Authentication authentication) {

    	// >> 권한 가져오기 <<
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // >> Access Token (액세스 토큰) 유효기간. 현재로 부터 60분 <<
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);


        // >> Access Token 생성 <<
        String accessToken = Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .expiration(accessTokenExpiresIn)
                .signWith(secretKey)
                .compact();

        // >> Refresh Token 생성 <<
        String refreshToken = Jwts.builder()
                .expiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();

        // >> "JWT 토큰 생성하기" <<
        return JwtToken.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }



    /*
        >> 실제 인증에 사용되는 JWT 문자열인 accessToken 을 복호화(해독) 하여
           accessToken에 들어있는 정보를 꺼내오는 메서드 <<
    */
    public Authentication getAuthentication(String accessToken) {

    	Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

     // 클레임에서 권한정보(역할) 가져오기
        Collection<? extends GrantedAuthority> authorities =
        		Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                      .map(SimpleGrantedAuthority::new)
                      .collect(Collectors.toList());

        String memberid = claims.getSubject();

        // MemberDTO 생성
        MemberDTO memberDto = new MemberDTO();
        memberDto.setMemberid(memberid);

        // 권한 세팅
        memberDto.setAuthorities(
                authorities.stream()
                           .map(GrantedAuthority::getAuthority)
                           .collect(Collectors.toList())
        );

        // CustomUserDetails 생성
        CustomUserDetails principal = new CustomUserDetails(memberDto);

        return new UsernamePasswordAuthenticationToken(
                principal,
                "",
                principal.getAuthorities()
        );
    }


    /*
       >> 토큰 정보를 검증하는 메서드 <<
    */
    public boolean validateToken(String token) {

    	try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token : ", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token : ", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token : ", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty : ", e);
        }

        return false;
    }



   /*
     >> 실제 인증에 사용되는 JWT 문자열인 accessToken 에서 클레임(Claims)을 추출하는 메서드 <<
   */
    private Claims parseClaims(String accessToken) {

        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


}
