package com.spring.app.product.domain;

import lombok.Data;

@Data
public class ProductPriceTrendDTO {

    private String priceDate;
    private Integer listAvgPrice;
    private Integer saleAvgPrice;
}