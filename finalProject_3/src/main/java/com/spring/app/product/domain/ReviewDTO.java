package com.spring.app.product.domain;

import java.util.Date;

import lombok.Data;

@Data
public class ReviewDTO {

    private Integer reviewNo;
    private String writerEmail;
    private String targetEmail;
    private Integer transactionId;
    private Double rating;
    private String oneLineCat;
    private String reviewContent;
    private Date createdAt;

    private String writerName;
    private String writerProfileImg;

    private String productName;
    private String productImgUrl;
    private String productImgUrls;
}