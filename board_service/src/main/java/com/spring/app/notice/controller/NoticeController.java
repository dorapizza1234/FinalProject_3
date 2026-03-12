package com.spring.app.notice.controller;

import com.spring.app.auth.domain.CustomUserDetails;
import com.spring.app.notice.domain.NoticeDTO;
import com.spring.app.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice/")
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 + FAQ 목록 (공개)
    @GetMapping("list")
    public String list(Model model) {
        model.addAttribute("noticeList", noticeService.getNoticeList());
        model.addAttribute("faqList", noticeService.getFaqList());
        return "notice/list";
    }

    // 상세 보기 (공개)
    @GetMapping("view")
    public String view(@RequestParam Long noticeId, Model model) {
        model.addAttribute("notice", noticeService.getNotice(noticeId));
        return "notice/view";
    }

    // 작성 폼 (관리자)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("write")
    public String writeForm(Model model) {
        return "notice/write";
    }

    // 작성 완료 (관리자)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("write")
    @ResponseBody
    public int writeSubmit(NoticeDTO dto,
                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            dto.setAdminEmail(userDetails.getUsername());
            noticeService.saveNotice(dto);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 삭제 (관리자, soft delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("delete")
    @ResponseBody
    public int delete(@RequestParam Long noticeId) {
        try {
            noticeService.deleteNotice(noticeId);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }
}
