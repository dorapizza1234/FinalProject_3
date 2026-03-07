package com.spring.app.product.domain;

import lombok.Data;

@Data
public class SearchLogDTO {

    private int searchId;
    private String keyword;
    private String searchType;

    private String memberEmail;
    private String guestId;
    private String sessionId;

    private String ipAddress;
    private String userAgent;
}