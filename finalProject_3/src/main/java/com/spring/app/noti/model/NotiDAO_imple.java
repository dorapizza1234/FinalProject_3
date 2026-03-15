package com.spring.app.noti.model;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.spring.app.noti.domain.NotiDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotiDAO_imple implements NotiDAO {

    @Qualifier("sqlsession")
    private final SqlSessionTemplate sqlsession;

    private static final String ns = "noti";

    @Override
    public List<NotiDTO> getNotifications(String email) {
        return sqlsession.selectList(ns + ".getNotifications", email);
    }

    @Override
    public int getUnreadCount(String email) {
        return sqlsession.selectOne(ns + ".getUnreadCount", email);
    }

    @Override
    public void markAsRead(int notiNo) {
        sqlsession.update(ns + ".markAsRead", notiNo);
    }

    @Override
    public void markAllAsRead(String email) {
        sqlsession.update(ns + ".markAllAsRead", email);
    }
}
