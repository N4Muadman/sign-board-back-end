package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.DesignTemplate;
import com.techbytedev.signboardmanager.repository.DesignTemplateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DesignTemplateService {

    private final DesignTemplateRepository designTemplateRepository;

    public DesignTemplateService(DesignTemplateRepository designTemplateRepository) {
        this.designTemplateRepository = designTemplateRepository;
    }

    public DesignTemplate tao(DesignTemplate designTemplate) {
        return designTemplateRepository.save(designTemplate);
    }

    public List<DesignTemplate> layTatCa() {
        return designTemplateRepository.findAllByDeletedAtIsNull();
    }

    public DesignTemplate layTheoId(Long id) {
        return designTemplateRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
    }

    public DesignTemplate capNhat(DesignTemplate designTemplate) {
        DesignTemplate existingTemplate = layTheoId(designTemplate.getId());
        if (existingTemplate == null) {
            return null;
        }
        existingTemplate.setPreviewImageUrl(designTemplate.getPreviewImageUrl());
        existingTemplate.setCanvasTemplateLink(designTemplate.getCanvasTemplateLink());
        return designTemplateRepository.save(existingTemplate);
    }

    public boolean xoa(Long id) {
        DesignTemplate designTemplate = layTheoId(id);
        if (designTemplate == null) {
            return false;
        }
        designTemplate.setDeletedAt(LocalDateTime.now());
        designTemplateRepository.save(designTemplate);
        return true;
    }
}