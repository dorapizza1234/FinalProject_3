package com.spring.app.noti.service;

import java.util.List;
import com.spring.app.noti.domain.NotiDTO;

public interface NotiService {
    List<NotiDTO> getNotifications(String email);
    int getUnreadCount(String email);
    void markAsRead(int notiNo);
    void markAllAsRead(String email);
}
