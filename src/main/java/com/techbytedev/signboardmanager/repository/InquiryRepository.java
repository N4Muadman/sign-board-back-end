package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    Page<Inquiry> findAll(Pageable pageable);
}
