package com.spring.app.noti.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spring.app.noti.domain.NotiDTO;
import com.spring.app.noti.model.NotiDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotiService_imple implements NotiService {

    private final NotiDAO dao;

    @Override
    public List<NotiDTO> getNotifications(String email) {
        return dao.getNotifications(email);
    }

    @Override
    public int getUnreadCount(String email) {
        return dao.getUnreadCount(email);
    }

    @Override
    public void markAsRead(int notiNo) {
        dao.markAsRead(notiNo);
    }

    @Override
    public void markAllAsRead(String email) {
        dao.markAllAsRead(email);
    }
}
