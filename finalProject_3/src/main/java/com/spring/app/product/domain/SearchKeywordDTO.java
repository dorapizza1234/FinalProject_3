package com.spring.app.product.domain;

import lombok.Data;

@Data
public class SearchKeywordDTO {

    private String keyword;
    private int searchCount;
}