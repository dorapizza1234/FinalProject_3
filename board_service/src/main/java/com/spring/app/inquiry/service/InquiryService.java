package com.spring.app.inquiry.service;

import com.spring.app.inquiry.domain.InquiryDTO;
import java.util.List;

public interface InquiryService {
    void saveInquiry(InquiryDTO dto);
    List<InquiryDTO> getMyInquiries(String memberEmail);
    List<InquiryDTO> getAllInquiries();
    InquiryDTO getInquiry(Long inquiryId);
    void saveAnswer(Long inquiryId, String adminAnswer);
}
