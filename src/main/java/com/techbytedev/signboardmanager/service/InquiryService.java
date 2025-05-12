package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.InquiryRequest;
import com.techbytedev.signboardmanager.entity.Inquiry;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.repository.InquiryRepository;
import com.techbytedev.signboardmanager.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    public InquiryService(InquiryRepository inquiryRepository, UserRepository userRepository) {
        this.inquiryRepository = inquiryRepository;
        this.userRepository = userRepository;
    }
    public Inquiry createInquiry( InquiryRequest request) {
        Inquiry inquiry = new Inquiry();
        inquiry.setName(request.getName());
        inquiry.setPhone(request.getPhone());
        inquiry.setEmail(request.getEmail());
        inquiry.setAddress(request.getAddress());
        inquiry.setMessage(request.getMessage());
        inquiry.setProductIds(request.getProductIds());
        inquiry.setCreatedAt(LocalDateTime.now());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            inquiry.setUser(user);
        } else {
            inquiry.setUser(null);
        }

        return inquiryRepository.save(inquiry);
    }
    public Page<Inquiry> getAllInquiries(Pageable pageable) {
        return inquiryRepository.findAll(pageable);
    }
}
