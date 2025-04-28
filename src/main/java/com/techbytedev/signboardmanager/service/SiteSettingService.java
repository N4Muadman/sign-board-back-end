package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.SiteSetting;
import com.techbytedev.signboardmanager.repository.SiteSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteSettingService {
    @Autowired
    private SiteSettingRepository siteSettingRepository;

    public List<SiteSetting> getAllSiteSettings() {
        return siteSettingRepository.findAll();
    }
    // sửa nội dung
    public SiteSetting updateSiteSetting(String key, SiteSetting siteSetting) {
        SiteSetting existingSite = siteSettingRepository.findById(key)
                .orElseThrow(() -> new RuntimeException("Không tìm nội dung"));
        existingSite.setValue(siteSetting.getValue());
        existingSite.setDescription(siteSetting.getDescription());
        existingSite.setPublic(siteSetting.isPublic());
        return siteSettingRepository.save(existingSite);
    }
}
