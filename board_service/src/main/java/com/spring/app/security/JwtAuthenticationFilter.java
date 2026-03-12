package com.spring.app.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/*
 - 클라이언트가 접속하는 모든 페이지 마다 JWT 인증을 하기 위한 커스텀 필터로서,
   UsernamePasswordAuthenticationFilter 이전에 실행 할 것이다.

 - 클라이언트에서 요청을 보내온 Http Request Header 에서 JWT 토큰을 추출하여,
   유효한 토큰인 경우라면 해당 토큰에서 사용자 인증 정보(Authentication)를 가져와서,
   SecurityContext에 저장함으로 인증 요청을 마무리 하도록 한다.
*/
@RequiredArgsConstructor

public class JwtAuthenticationFilter extends GenericFilterBean {

	public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

System.out.println("##### 확인용 board-service : JwtAuthenticationFilter 의 doFilter() 메서드가 작동함.");

		// 클라이언트에서 요청을 보내온 Http Request Header에서 JWT 토큰 추출
		String token = resolveToken((HttpServletRequest) request);

		if (token != null && jwtTokenProvider.validateToken(token)) {
			Authentication authentication = jwtTokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		chain.doFilter(request, response); // 다음 필터로 요청을 전달

	}


	/*
	    Request Header 에서 토큰 정보 추출하기
	 */
	private String resolveToken(HttpServletRequest request) {
		// 1. Authorization 헤더에서 추출 (fetch/ajax 요청)
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.split(" ")[1].trim();
		}

		// 2. HttpOnly 쿠키에서 추출 (브라우저 페이지 이동)
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("accessToken".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}

		return null;
	}
}
