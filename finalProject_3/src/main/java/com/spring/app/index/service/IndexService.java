package com.spring.app.index.service;

import java.util.List;

import com.spring.app.product.domain.ProductDTO;

public interface IndexService {

    List<ProductDTO> getMainLatestList(String loginUserEmail);

    List<ProductDTO> getMainRecommendList(String loginUserEmail);

    List<ProductDTO> getMainFreeList(String loginUserEmail);
}