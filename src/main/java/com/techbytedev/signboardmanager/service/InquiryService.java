package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.InquiryRequest;
import com.techbytedev.signboardmanager.dto.response.InquiryResponse;
import com.techbytedev.signboardmanager.dto.response.UserResponse;
import com.techbytedev.signboardmanager.entity.Inquiry;
import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.repository.InquiryRepository;
import com.techbytedev.signboardmanager.repository.ProductRepository;
import com.techbytedev.signboardmanager.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InquiryService {
    private static final Logger logger = LoggerFactory.getLogger(InquiryService.class);

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService; // Thêm EmailService
    private final UserService userService;

    public InquiryService(InquiryRepository inquiryRepository, UserRepository userRepository, 
                         ProductRepository productRepository, EmailService emailService, UserService userService) {
        this.inquiryRepository = inquiryRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.emailService = emailService; // Khởi tạo EmailService
        this.userService = userService;
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

        // Gửi email thông báo đến admin
        sendEmailToAdmins(saved, product);

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

    private void sendEmailToAdmins(Inquiry inquiry, Product product) {
       // Tìm tất cả admin
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<UserResponse> adminUsers = userService.searchUsers(null, null, "Admin", true, pageable);
        List<String> adminEmails = adminUsers.getContent().stream()
                .map(UserResponse::getEmail)
                .filter(email -> email != null && !email.equals("N/A"))
                .collect(Collectors.toList());

        // Định dạng thời gian gửi
        String submissionTime = inquiry.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        // Gửi email đến từng admin
        for (String adminEmail : adminEmails) {
            String emailContent = String.format(
                "<h3>Thông báo: Liên hệ mới được gửi</h3>" +
                "<p><strong>ID liên hệ:</strong> %d</p>" +
                "<p><strong>Người gửi:</strong> %s</p>" +
                "<p><strong>Email:</strong> %s</p>" +
                "<p><strong>Số điện thoại:</strong> %s</p>" +
                "<p><strong>Địa chỉ:</strong> %s</p>" +
                "<p><strong>Thời gian gửi:</strong> %s</p>" +
                "<p><strong>Sản phẩm:</strong> %s</p>" +
                "<p><strong>Nội dung:</strong> %s</p>",
                inquiry.getInquiryId(),
                inquiry.getName(),
                inquiry.getEmail() != null ? inquiry.getEmail() : "Không có email",
                inquiry.getPhone() != null ? inquiry.getPhone() : "Không có số điện thoại",
                inquiry.getAddress() != null ? inquiry.getAddress() : "Không có địa chỉ",
                submissionTime,
                product.getName(),
                inquiry.getMessage() != null ? inquiry.getMessage() : "Không có nội dung"
            );

            try {
                emailService.sendEmail(adminEmail, "Thông báo liên hệ mới", emailContent, null);
                logger.info("Gửi email thông báo liên hệ đến: {}", adminEmail);
            } catch (MessagingException e) {
                logger.error("Lỗi gửi email đến {}: {}", adminEmail, e.getMessage());
            }
        }
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
        response.setStatus(inquiry.getStatus() != null ? inquiry.getStatus() : "NOCONTACT");
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