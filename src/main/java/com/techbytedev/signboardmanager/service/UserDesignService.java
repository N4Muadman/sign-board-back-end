package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.UserDesign;
import com.techbytedev.signboardmanager.entity.UserDesignTemplate;
import com.techbytedev.signboardmanager.repository.UserDesignRepository;
import com.techbytedev.signboardmanager.repository.UserDesignTemplateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserDesignService {

    private final UserDesignRepository userDesignRepository;
    private final UserDesignTemplateRepository userDesignTemplateRepository;

    public UserDesignService(UserDesignRepository userDesignRepository, UserDesignTemplateRepository userDesignTemplateRepository) {
        this.userDesignRepository = userDesignRepository;
        this.userDesignTemplateRepository = userDesignTemplateRepository;
    }

    public UserDesign tao(UserDesign userDesign) {
        return userDesignRepository.save(userDesign);
    }

    public List<UserDesign> layTatCa() {
        return userDesignRepository.findAllByDeletedAtIsNull();
    }

    public UserDesign layTheoId(Long id) {
        return userDesignRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
    }

    public UserDesign capNhat(UserDesign userDesign) {
        UserDesign existingDesign = layTheoId(userDesign.getId());
        if (existingDesign == null) return null;
        existingDesign.setDesignImage(userDesign.getDesignImage());
        existingDesign.setDesignLink(userDesign.getDesignLink());
        return userDesignRepository.save(existingDesign);
    }

    public boolean xoa(Long id) {
        UserDesign userDesign = layTheoId(id);
        if (userDesign == null) return false;
        userDesign.setDeletedAt(LocalDateTime.now());
        userDesignRepository.save(userDesign);
        return true;
    }

    public void ganMauThietKe(Long designId, List<Long> templateIds) {
        UserDesign userDesign = layTheoId(designId);
        if (userDesign == null) throw new IllegalArgumentException("Thiết kế không tồn tại");
        for (Long templateId : templateIds) {
            UserDesignTemplate relation = new UserDesignTemplate();
            relation.setUserDesignId(designId);
            relation.setDesignTemplateId(templateId);
            userDesignTemplateRepository.save(relation);
        }
    }

    public void goMauThietKe(Long designId, List<Long> templateIds) {
        UserDesign userDesign = layTheoId(designId);
        if (userDesign == null) throw new IllegalArgumentException("Thiết kế không tồn tại");
        for (Long templateId : templateIds) {
            userDesignTemplateRepository.deleteByUserDesignIdAndDesignTemplateId(designId, templateId);
        }
    }
}