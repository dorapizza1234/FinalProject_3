package com.spring.app.admin.service;

import java.util.List;
import java.util.Map;

import com.spring.app.admin.ad.domain.AdDTO;
import com.spring.app.product.domain.ProductDTO;
import com.spring.app.security.domain.MemberDTO;

public interface AdminService {

	int registerAd(AdDTO adDto); //광고 신청 insert하기 

	 List<MemberDTO> getMemberList();//

	 int getNewMembersCount();  //신규가입자 가져오기

	 int getWithdrawalsCount(); //탈퇴수 가져오기

	 int getTotalMembersCount(); //모든 회원 수 

	 MemberDTO getMemberByNo(int userNo);

	 List<MemberDTO> getMemberList(int page, int size); //회원  보여주기

	 List<ProductDTO> getProductList(int page, int size); //상품 리스트 보여주기

	 int getTotalProductsCount(); //총상품개수 

	 int getOnsaleProductCount(); //판매중인 상품
	 
	 List<AdDTO> getAdList();//광고리스트 가져오기

	 AdDTO getAd(Long adId);//광고 상세보여주기

	 void approvedAd(Long adId); //광고 승인하기

	 void rejectAd(Long adId, String reason);//광고반려하기

	 boolean checkAdConflict(String startDate, String endDate);

	 

}
