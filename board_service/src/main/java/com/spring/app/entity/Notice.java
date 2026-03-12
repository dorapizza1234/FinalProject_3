package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "NOTICES")
@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class Notice {

    @Id
    @Column(name = "NOTICE_ID")
    @SequenceGenerator(name = "SEQ_NOTICE_GENERATOR", sequenceName = "SEQ_NOTICE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NOTICE_GENERATOR")
    private Long noticeId;

    @Column(name = "ADMIN_EMAIL", length = 100)
    private String adminEmail;

    @Column(name = "TITLE", length = 400, nullable = false)
    private String title;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "VIEW_COUNT", columnDefinition = "NUMBER DEFAULT 0", insertable = false, updatable = false)
    private Integer viewCount;

    @Column(name = "IMPORTANCE", columnDefinition = "NUMBER(1) DEFAULT 0")
    private Integer importance;

    @Column(name = "STATUS", length = 20, columnDefinition = "VARCHAR2(20) DEFAULT 'PUBLISHED'")
    private String status;

    @Column(name = "IS_DELETED", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String isDeleted;

    @Column(name = "CREATED_AT", columnDefinition = "DATE DEFAULT SYSDATE", insertable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "NOTICE_TYPE", length = 20)
    private String noticeType; // 'NOTICE' or 'FAQ'
}
