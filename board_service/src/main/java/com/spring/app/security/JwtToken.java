package com.spring.app.security;

import lombok.*;

@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class JwtToken {
    private String grantType;
    private String accessToken;
    private Long accessTokenExpiresIn;
    private String refreshToken;
}
