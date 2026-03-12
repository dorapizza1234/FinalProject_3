package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "INQUIRIES")
@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class Inquiry {

    @Id
    @Column(name = "INQUIRY_ID")
    @SequenceGenerator(name = "SEQ_INQUIRY_GENERATOR", sequenceName = "SEQ_INQUIRY_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INQUIRY_GENERATOR")
    private Long inquiryId;

    @Column(name = "MEMBER_EMAIL", length = 100, nullable = false)
    private String memberEmail;

    @Column(name = "TITLE", length = 200, nullable = false)
    private String title;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "CREATED_AT", columnDefinition = "DATE DEFAULT SYSDATE", insertable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "INQUIRY_STATUS", length = 20)
    private String inquiryStatus; // '대기' or '답변완료'

    @Lob
    @Column(name = "ADMIN_ANSWER")
    private String adminAnswer;

    @Column(name = "ANSWERED_AT")
    private LocalDate answeredAt;
}
