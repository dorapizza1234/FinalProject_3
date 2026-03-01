package com.spring.app.product.model;

import org.apache.ibatis.annotations.Mapper;

import com.spring.app.product.domain.ProductDTO;
import com.spring.app.product.domain.ProductImageDTO;
import com.spring.app.product.domain.ProductMeetLocationDTO;
import com.spring.app.product.domain.ProductShippingOptionDTO;

@Mapper
public interface ProductDAO {

    int insertProduct(ProductDTO productDto);

    int insertProductImage(ProductImageDTO imageDto);

    int insertShippingOption(ProductShippingOptionDTO optionDto);

    int insertMeetLocation(ProductMeetLocationDTO locationDto);
}