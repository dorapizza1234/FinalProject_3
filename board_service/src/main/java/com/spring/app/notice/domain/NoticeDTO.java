package com.spring.app.notice.domain;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class NoticeDTO {
    private Long noticeId;
    private String adminEmail;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer importance;
    private String status;
    private String isDeleted;
    private LocalDate createdAt;
    private String noticeType; // 'NOTICE' or 'FAQ'
}
