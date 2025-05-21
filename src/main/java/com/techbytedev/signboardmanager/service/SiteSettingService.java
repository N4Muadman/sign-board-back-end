package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.SiteSetting;
import com.techbytedev.signboardmanager.repository.SiteSettingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteSettingService {
    private final SiteSettingRepository siteSettingRepository;

    public SiteSettingService(SiteSettingRepository siteSettingRepository) {
        this.siteSettingRepository = siteSettingRepository;
    }

    public Page<SiteSetting> getAllSiteSettings(Pageable pageable) {
        return siteSettingRepository.findAll(pageable);
    }
    // sửa nội dung
    public SiteSetting updateSiteSetting(int key, SiteSetting siteSetting) {
        SiteSetting existingSite = siteSettingRepository.findById(key)
                .orElseThrow(() -> new RuntimeException("Không tìm nội dung"));
        existingSite.setValue(siteSetting.getValue());
        existingSite.setDescription(siteSetting.getDescription());
        existingSite.setPublic(siteSetting.isPublic());
        return siteSettingRepository.save(existingSite);
    }
}
