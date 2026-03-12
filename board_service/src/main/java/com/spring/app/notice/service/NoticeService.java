package com.spring.app.notice.service;

import com.spring.app.notice.domain.NoticeDTO;
import java.util.List;

public interface NoticeService {
    List<NoticeDTO> getNoticeList();
    List<NoticeDTO> getFaqList();
    NoticeDTO getNotice(Long noticeId);
    void saveNotice(NoticeDTO dto);
    void deleteNotice(Long noticeId);
}
