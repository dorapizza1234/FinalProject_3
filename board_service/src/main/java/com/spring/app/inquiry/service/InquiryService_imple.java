package com.spring.app.inquiry.service;

import com.spring.app.entity.Inquiry;
import com.spring.app.inquiry.domain.InquiryDTO;
import com.spring.app.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryService_imple implements InquiryService {

    private final InquiryRepository inquiryRepository;

    @Override
    public void saveInquiry(InquiryDTO dto) {
        Inquiry inquiry = Inquiry.builder()
            .memberEmail(dto.getMemberEmail())
            .title(dto.getTitle())
            .content(dto.getContent())
            .inquiryStatus("대기")
            .build();
        inquiryRepository.save(inquiry);
    }

    @Override
    public List<InquiryDTO> getMyInquiries(String memberEmail) {
        return inquiryRepository.findByMemberEmailOrderByCreatedAtDesc(memberEmail)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<InquiryDTO> getAllInquiries() {
        return inquiryRepository.findAllByOrderByCreatedAtDesc()
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public InquiryDTO getInquiry(Long inquiryId) {
        return toDTO(inquiryRepository.findById(inquiryId)
            .orElseThrow(() -> new RuntimeException("문의가 없습니다.")));
    }

    @Override
    @Transactional
    public void saveAnswer(Long inquiryId, String adminAnswer) {
        inquiryRepository.findById(inquiryId).ifPresent(i -> {
            i.setAdminAnswer(adminAnswer);
            i.setAnsweredAt(LocalDate.now());
            i.setInquiryStatus("답변완료");
            inquiryRepository.save(i);
        });
    }

    private InquiryDTO toDTO(Inquiry i) {
        return InquiryDTO.builder()
            .inquiryId(i.getInquiryId())
            .memberEmail(i.getMemberEmail())
            .title(i.getTitle())
            .content(i.getContent())
            .createdAt(i.getCreatedAt())
            .inquiryStatus(i.getInquiryStatus())
            .adminAnswer(i.getAdminAnswer())
            .answeredAt(i.getAnsweredAt())
            .build();
    }
}
