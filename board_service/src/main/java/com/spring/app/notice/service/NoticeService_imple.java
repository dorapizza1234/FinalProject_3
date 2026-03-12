package com.spring.app.notice.service;

import com.spring.app.entity.Notice;
import com.spring.app.notice.domain.NoticeDTO;
import com.spring.app.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService_imple implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public List<NoticeDTO> getNoticeList() {
        return noticeRepository
            .findByNoticeTypeAndIsDeletedOrderByImportanceDescCreatedAtDesc("NOTICE", "N")
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<NoticeDTO> getFaqList() {
        return noticeRepository
            .findByNoticeTypeAndIsDeletedOrderByImportanceDescCreatedAtDesc("FAQ", "N")
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NoticeDTO getNotice(Long noticeId) {
        noticeRepository.incrementViewCount(noticeId);
        Notice notice = noticeRepository.findByNoticeIdAndIsDeleted(noticeId, "N")
            .orElseThrow(() -> new RuntimeException("공지사항이 없습니다."));
        return toDTO(notice);
    }

    @Override
    public void saveNotice(NoticeDTO dto) {
        Notice notice = Notice.builder()
            .noticeId(dto.getNoticeId())
            .adminEmail(dto.getAdminEmail())
            .title(dto.getTitle())
            .content(dto.getContent())
            .importance(dto.getImportance() != null ? dto.getImportance() : 0)
            .status("PUBLISHED")
            .isDeleted("N")
            .noticeType(dto.getNoticeType() != null ? dto.getNoticeType() : "NOTICE")
            .build();
        noticeRepository.save(notice);
    }

    @Override
    @Transactional
    public void deleteNotice(Long noticeId) {
        noticeRepository.findById(noticeId).ifPresent(n -> {
            n.setIsDeleted("Y");
            noticeRepository.save(n);
        });
    }

    private NoticeDTO toDTO(Notice n) {
        return NoticeDTO.builder()
            .noticeId(n.getNoticeId())
            .adminEmail(n.getAdminEmail())
            .title(n.getTitle())
            .content(n.getContent())
            .viewCount(n.getViewCount())
            .importance(n.getImportance())
            .status(n.getStatus())
            .isDeleted(n.getIsDeleted())
            .createdAt(n.getCreatedAt())
            .noticeType(n.getNoticeType())
            .build();
    }
}
