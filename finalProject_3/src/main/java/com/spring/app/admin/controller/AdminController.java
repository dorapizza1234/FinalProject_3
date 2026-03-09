package com.spring.app.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.app.admin.ad.domain.AdDTO;
import com.spring.app.admin.service.AdminService;
import com.spring.app.product.domain.ProductDTO;
import com.spring.app.security.domain.MemberDTO;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    
    //관리자 메인페이지
    @GetMapping("/index")
    public String index() {
    	return "admin/index";
    }
    		
    
    
    
    
    //=====================================================================================//
     //회원 관리 페이지
    @GetMapping("/member")
    public String memebrPage(Model model,
                             @RequestParam(value = "page", defaultValue = "1") int page)  {
        int size = 20;
        int totalMembers = adminService.getTotalMembersCount();
        int totalPages = (int) Math.ceil((double) totalMembers / size);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("members", adminService.getMemberList(page, size));
        model.addAttribute("newMembers", adminService.getNewMembersCount());
        model.addAttribute("withdrawals", adminService.getWithdrawalsCount());
        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/member";
    }
    
    //회원 한명 보여주기
    @GetMapping("/member/detail")
    @ResponseBody
    public MemberDTO getMemberDetail(@RequestParam("userNo")int userNo) {
    return adminService.getMemberByNo(userNo);	
    }
    
    //=====================================================================================//
    
   //상품리스트 가져오기
    @GetMapping("/product")
    public String productPage(Model model,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        // 1. 데이터 가져오기
        List<ProductDTO> productList = adminService.getProductList(page, size);
        int totalProducts = adminService.getTotalProductsCount();
        
        
        // 2. [추가] 실제 '판매중'인 상품 개수 가져오기
        int onsaleCount = adminService.getOnsaleProductCount(); // 서비스 호출
         
        // 2. 카테고리 매핑 (번호 -> 이름)
        Map<Integer, String> categoryMap = new HashMap<>();
        categoryMap.put(1, "패션");
        categoryMap.put(2, "육아");
        categoryMap.put(3, "가전");
        categoryMap.put(4, "홈·인테리어");
        categoryMap.put(5, "취미");
        categoryMap.put(6, "여행");
        categoryMap.put(7, "공구/산업용품");

        for(ProductDTO p : productList) {
            p.setAreaGu(categoryMap.getOrDefault(p.getCategoryNo(), "기타")); 
        }

        // 3. 모델 담기
        model.addAttribute("productList", productList);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("size", size);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) totalProducts / size));
        
        // 차트 및 통계용 (에러 방지용 임시값)
        model.addAttribute("reportCount", 0);
        model.addAttribute("onsaleCount", onsaleCount); // DB 연결 필요
        model.addAttribute("chartData", Arrays.asList(5, 10, 8, 15, 20, 12, 7)); // 월~일 데이터

        return "admin/product"; 
    }
     
    
    
    //=====================================================================================//
    
    //=====================================================================================//  
    //광고 페이지 보여주기
    @GetMapping("/ad")
    public String adPage() {
    	return "admin/ad";
    }
     //광고 등록하기
    @PostMapping("/ad/register")
    @ResponseBody
    public Map<String,Object>registerAd(AdDTO adDto){
    	Map<String,Object>map=new HashMap<>();
    	try {
    		adminService.registerAd(adDto); 
            map.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("error", e.getMessage());
        }
        return map;
    }
    //=====================================================================================//
    	
    //컨텐츠 관리페이지  

    @GetMapping("/contents")
    public String getDashboard(Model model) {
    	
    	List<AdDTO>adList=adminService.getAdList();
    	model.addAttribute("adList",adList);
    	
    	LocalDate today = LocalDate.now();
        List<List<LocalDate>> calendarWeeks = generateCalendar(today);
        
        model.addAttribute("calendarWeeks", calendarWeeks);
        model.addAttribute("today", today);

        return "admin/contents";
    	
    	
    }
    
    //광고 상세보기
    
    @GetMapping("/ad/detail")
    @ResponseBody
    public Map<String,Object> getAdDetail(@RequestParam("adId") Long adId) {

        AdDTO ad = adminService.getAd(adId);

        Map<String,Object> map = new HashMap<>();

        if(ad == null){
            map.put("ad", null);
            map.put("conflict", false);
            return map;
        }

        boolean conflict = adminService.checkAdConflict(ad.getStartDate(), ad.getEndDate());

        map.put("ad", ad);
        map.put("conflict", conflict);

        return map;
    }
    
    @PostMapping("/ad/approve")
    @ResponseBody
    public String approvedAd(@RequestParam("adId") Long adId) {
    	adminService.approvedAd(adId);
    	return "ok";
    }
    
    
    @PostMapping("/ad/reject")
    @ResponseBody
    public String rejectAd(@RequestParam("adId") Long adId,
            @RequestParam("reason") String reason) {

    	adminService.rejectAd(adId, reason);

        return "ok";
    }
    
 // ---------------------------------------------------------
    // [추가할 부분] 달력 데이터를 만드는 로직 (에러 해결용)
    // ---------------------------------------------------------
    private List<List<LocalDate>> generateCalendar(LocalDate date) {
        List<List<LocalDate>> weeks = new ArrayList<>();
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate lastDayOfMonth = date.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());

        // 시작 요일 (월=1, ..., 일=7) -> 일요일 시작으로 맞추려면 조정 필요
        int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); 
        // 월요일 시작 기준일 때 1일 이전의 월요일로 이동
        LocalDate current = firstDayOfMonth.minusDays(startDayOfWeek - 1);

        // 최소 5주는 출력하도록 설정
        for (int i = 0; i < 6; i++) { // 최대 6주까지
            List<LocalDate> week = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                week.add(current);
                current = current.plusDays(1);
            }
            weeks.add(week);
            // 마지막 날짜가 지났고, 주가 꽉 찼다면 중단
            if (current.isAfter(lastDayOfMonth)) break;
        }
        return weeks;
    }
    //문의 페이지 보여주기
    @GetMapping("/inquiry")
    public String inquiry() {
    	return "admin/inquiry";
    }
    
    
    //신고 페이지 보여주기
    @GetMapping("/complaint")
    public String complaint() {
    	return "admin/complaint";
    }
    
    //신고 페이지 보여주기
    @GetMapping("/transaction")
    public String transaction() {
    	return "admin/transaction";
    }
    
    //신고 페이지 보여주기
    @GetMapping("/review")
    public String review() {
    	return "admin/review";
    }
}