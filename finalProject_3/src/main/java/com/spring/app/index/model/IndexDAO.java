package com.spring.app.index.model;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.product.domain.ProductDTO;

@Mapper
public interface IndexDAO {

    List<ProductDTO> getMainLatestList(@Param("loginUserEmail") String loginUserEmail);

    List<ProductDTO> getMainRecommendList(@Param("loginUserEmail") String loginUserEmail);

    List<ProductDTO> getMainFreeList(@Param("loginUserEmail") String loginUserEmail);
}