package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.entity.SiteSetting;
import com.techbytedev.signboardmanager.service.SiteSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/site-setting")
public class SiteSettingController {

    private final SiteSettingService siteSettingService;

    public SiteSettingController(SiteSettingService siteSettingService) {
        this.siteSettingService = siteSettingService;
    }

    //ADMIN & CUSTOMER
    // hiển thị
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<SiteSetting> siteSettingPage = siteSettingService.getAllSiteSettings(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", siteSettingPage.getContent());
        response.put("pageNumber", siteSettingPage.getNumber() + 1);
        response.put("pageSize", siteSettingPage.getSize());
        response.put("totalPages", siteSettingPage.getTotalPages());
        response.put("totalElements", siteSettingPage.getTotalElements());
        response.put("last", siteSettingPage.isLast());

        return ResponseEntity.ok(response);
    }
}
