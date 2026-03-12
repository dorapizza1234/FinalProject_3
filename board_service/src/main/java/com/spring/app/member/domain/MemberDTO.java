package com.spring.app.member.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class MemberDTO {
    private String memberid;
    private String passwd;
    private String enabled;
    private String name;
    private LocalDate registerday;
    private LocalDateTime lastLoginDate;
    private List<String> authorities;
}
