package com.spring.app.product.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ProductPriceTrendDTO {

    private String priceDate;
    private Integer avgPrice;
}