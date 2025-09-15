package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.request.InquiryRequest;
import com.techbytedev.signboardmanager.entity.Inquiry;
import com.techbytedev.signboardmanager.service.InquiryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiries/")
public class InquiryController {
    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    // thêm liên hệ
    @PostMapping("/create")
    public ResponseEntity<Inquiry> createInquiry(@RequestBody InquiryRequest request) {
        try {
            Inquiry inquiry = inquiryService.createInquiry(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(inquiry);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
