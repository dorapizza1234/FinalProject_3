package com.spring.app.noti.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.app.noti.domain.NotiDTO;
import com.spring.app.noti.service.NotiService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/noti")
@RequiredArgsConstructor
public class NotiController {

    private final NotiService notiService;

    /** 알림 목록 페이지 */
    @GetMapping("/list")
    public String list(Model model, Principal principal) {
        String email = principal.getName();
        model.addAttribute("notiList", notiService.getNotifications(email));
        model.addAttribute("unreadCount", notiService.getUnreadCount(email));
        return "noti/noti_list";
    }

    /** 알림 미리보기 (mypage용 JSON) */
    @GetMapping("/preview")
    @ResponseBody
    public List<NotiDTO> preview(Principal principal) {
        if (principal == null) return new ArrayList<>();
        return notiService.getNotifications(principal.getName());
    }

    /** 미읽음 개수 (헤더 배지 초기값용) */
    @GetMapping("/count")
    @ResponseBody
    public Map<String, Object> count(Principal principal) {
        Map<String, Object> result = new HashMap<>();
        if (principal == null) { result.put("count", 0); return result; }
        result.put("count", notiService.getUnreadCount(principal.getName()));
        return result;
    }

    /** 단건 읽음 처리 */
    @PostMapping("/read/{notiNo}")
    @ResponseBody
    public Map<String, Object> markAsRead(@PathVariable int notiNo) {
        Map<String, Object> result = new HashMap<>();
        try {
            notiService.markAsRead(notiNo);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
        }
        return result;
    }

    /** 전체 읽음 처리 */
    @PostMapping("/readAll")
    @ResponseBody
    public Map<String, Object> markAllAsRead(Principal principal) {
        Map<String, Object> result = new HashMap<>();
        try {
            notiService.markAllAsRead(principal.getName());
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
        }
        return result;
    }
}
