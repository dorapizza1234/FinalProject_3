package com.spring.app.product.domain;

import lombok.Data;

@Data
public class ReviewSummaryDTO {

    private Integer totalCount;
    private Double avgRating;

    private Integer kindCount;
    private Integer promiseCount;
    private Integer sameDescCount;
    private Integer carefulCount;
}