package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.InquiryRequest;
import com.techbytedev.signboardmanager.dto.response.InquiryResponse;
import com.techbytedev.signboardmanager.entity.Inquiry;
import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.repository.InquiryRepository;
import com.techbytedev.signboardmanager.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    public InquiryService(InquiryRepository inquiryRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.inquiryRepository = inquiryRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public InquiryResponse createInquiry(InquiryRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Inquiry inquiry = new Inquiry();
        inquiry.setName(request.getName());
        inquiry.setPhone(request.getPhone());
        inquiry.setEmail(request.getEmail());
        inquiry.setAddress(request.getAddress());
        inquiry.setMessage(request.getMessage());
        inquiry.setProduct(product);
        inquiry.setCreatedAt(LocalDateTime.now());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            inquiry.setUser(user);
        } else {
            inquiry.setUser(null);
        }

        Inquiry saved = inquiryRepository.save(inquiry);

        InquiryResponse response = new InquiryResponse();
        response.setId(saved.getInquiryId());
        response.setName(saved.getName());
        response.setPhone(saved.getPhone());
        response.setEmail(saved.getEmail());
        response.setAddress(saved.getAddress());
        response.setMessage(saved.getMessage());
        response.setCreatedAt(saved.getCreatedAt());
        response.setProductName(product.getName());

        return response;
    }
    public InquiryResponse convertToResponse(Inquiry inquiry) {
        InquiryResponse response = new InquiryResponse();
        response.setId(inquiry.getInquiryId());
        response.setName(inquiry.getName());
        response.setPhone(inquiry.getPhone());
        response.setEmail(inquiry.getEmail());
        response.setAddress(inquiry.getAddress());
        response.setMessage(inquiry.getMessage());
        response.setCreatedAt(inquiry.getCreatedAt());
        response.setProductName(inquiry.getProduct() != null ? inquiry.getProduct().getName() : null);
        response.setStatus(inquiry.getStatus());
        return response;
    }

    public void updateInquiryStatus(Integer id, String status) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy inquiry"));
        inquiry.setStatus(status);
        inquiryRepository.save(inquiry);
    }

    public Page<Inquiry> getAllInquiries(Pageable pageable) {
        return inquiryRepository.findAll(pageable);
    }
}
