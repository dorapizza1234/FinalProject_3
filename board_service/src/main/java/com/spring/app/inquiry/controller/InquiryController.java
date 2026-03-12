package com.spring.app.inquiry.controller;

import com.spring.app.auth.domain.CustomUserDetails;
import com.spring.app.inquiry.domain.InquiryDTO;
import com.spring.app.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inquiry/")
public class InquiryController {

    private final InquiryService inquiryService;

    // 내 문의 목록 (비로그인 허용, JS로 UI 제어)
    @GetMapping("list")
    public String list(Model model,
                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            model.addAttribute("inquiryList", inquiryService.getMyInquiries(userDetails.getUsername()));
        } else {
            model.addAttribute("inquiryList", java.util.Collections.emptyList());
        }
        return "inquiry/list";
    }

    // 문의 작성 폼 (GET은 허용, POST는 JWT 필터가 인증 처리)
    @GetMapping("write")
    public String writeForm() {
        return "inquiry/write";
    }

    // 문의 작성 완료
    @PreAuthorize("isAuthenticated()")
    @PostMapping("write")
    @ResponseBody
    public int writeSubmit(InquiryDTO dto,
                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            dto.setMemberEmail(userDetails.getUsername());
            inquiryService.saveInquiry(dto);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 상세 보기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("view")
    public String view(@RequestParam Long inquiryId, Model model,
                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        InquiryDTO dto = inquiryService.getInquiry(inquiryId);
        // 본인 문의 또는 관리자만
        if (!dto.getMemberEmail().equals(userDetails.getUsername()) &&
            !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/inquiry/list";
        }
        model.addAttribute("inquiry", dto);
        return "inquiry/view";
    }

    // 관리자: 전체 문의 목록
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("admin/list")
    public String adminList(Model model) {
        model.addAttribute("inquiryList", inquiryService.getAllInquiries());
        return "inquiry/admin_list";
    }

    // 관리자: 답변 저장
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("admin/answer")
    @ResponseBody
    public int saveAnswer(@RequestParam Long inquiryId,
                          @RequestParam String adminAnswer) {
        try {
            inquiryService.saveAnswer(inquiryId, adminAnswer);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }
}
