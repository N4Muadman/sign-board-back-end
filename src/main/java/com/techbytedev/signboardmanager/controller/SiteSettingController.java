package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.entity.SiteSetting;
import com.techbytedev.signboardmanager.service.SiteSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/site-setting")
public class SiteSettingController {
    @Autowired
    private SiteSettingService siteSettingService;

    //ADMIN & CUSTOMER
    // hiển thị
    @GetMapping("/list")
    public ResponseEntity<List<SiteSetting>> getList(){
        return ResponseEntity.ok(siteSettingService.getAllSiteSettings());
    }
    //ADMIN
    // sửa nội dung
    @PutMapping("/edit/{key}")
    public SiteSetting updateSiteSetting(@PathVariable("key") String key, @RequestBody SiteSetting siteSetting){
        return siteSettingService.updateSiteSetting(key, siteSetting);
    }
}
