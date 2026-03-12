package com.spring.app.inquiry.domain;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class InquiryDTO {
    private Long inquiryId;
    private String memberEmail;
    private String title;
    private String content;
    private LocalDate createdAt;
    private String inquiryStatus;
    private String adminAnswer;
    private LocalDate answeredAt;
}
