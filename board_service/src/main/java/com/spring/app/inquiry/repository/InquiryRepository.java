package com.spring.app.inquiry.repository;

import com.spring.app.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByMemberEmailOrderByCreatedAtDesc(String memberEmail);
    List<Inquiry> findAllByOrderByCreatedAtDesc();
}
