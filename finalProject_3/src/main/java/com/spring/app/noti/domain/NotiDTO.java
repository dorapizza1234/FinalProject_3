package com.spring.app.noti.domain;

import lombok.Data;

@Data
public class NotiDTO {
    private int notiNo;
    private int userNo;
    private String notiType;
    private String title;
    private String message;
    private String regDate;
    private int readStatus;
}
