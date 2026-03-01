package com.spring.app.product.service;

import java.util.List;

import com.spring.app.product.domain.ProductDTO;
import com.spring.app.product.domain.ProductImageDTO;
import com.spring.app.product.domain.ProductMeetLocationDTO;
import com.spring.app.product.domain.ProductShippingOptionDTO;

public interface ProductService {

    int productSellRegister(ProductDTO productDto,
                            List<ProductImageDTO> imageDtoList,
                            List<ProductShippingOptionDTO> shippingOptionList,
                            List<ProductMeetLocationDTO> meetLocationList);
}