package com.spring.app.index.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spring.app.index.model.IndexDAO;
import com.spring.app.product.domain.ProductDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndexService_imple implements IndexService {

    private final IndexDAO dao;

    @Override
    public List<ProductDTO> getMainLatestList(String loginUserEmail) {
        return dao.getMainLatestList(loginUserEmail);
    }

    @Override
    public List<ProductDTO> getMainRecommendList(String loginUserEmail) {
        return dao.getMainRecommendList(loginUserEmail);
    }

    @Override
    public List<ProductDTO> getMainFreeList(String loginUserEmail) {
        return dao.getMainFreeList(loginUserEmail);
    }
}