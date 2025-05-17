package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.UserDesign;
import com.techbytedev.signboardmanager.entity.UserDesignTemplate;
import com.techbytedev.signboardmanager.entity.UserDesignTemplateId;
import com.techbytedev.signboardmanager.repository.UserDesignRepository;
import com.techbytedev.signboardmanager.repository.UserDesignTemplateRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserDesignService {

    private final UserDesignRepository userDesignRepository;
    private final UserDesignTemplateRepository userDesignTemplateRepository;
    private final UserService userService;

    public UserDesignService(UserDesignRepository userDesignRepository, UserDesignTemplateRepository userDesignTemplateRepository, UserService userService) {
        this.userDesignRepository = userDesignRepository;
        this.userDesignTemplateRepository = userDesignTemplateRepository;
        this.userService = userService;
    }

    public UserDesign tao(UserDesign userDesign) {
        return userDesignRepository.save(userDesign);
    }

   public Page<UserDesign> layTatCa(Pageable pageable) {
        return userDesignRepository.findAllByDeletedAtIsNull(pageable);
    }

    public UserDesign layTheoId(Long id) {
        return userDesignRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
    }

    public UserDesign capNhat(UserDesign userDesign) {
        UserDesign existingDesign = layTheoId(userDesign.getId());
        if (existingDesign == null) return null;
        existingDesign.setDesignImage(userDesign.getDesignImage());
        existingDesign.setDesignLink(userDesign.getDesignLink());
        existingDesign.setStatus(userDesign.getStatus());
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
            userDesignTemplateRepository.deleteById(new UserDesignTemplateId(designId, templateId));
        }
    }

    // Kiểm tra quyền: Người dùng chỉ được thao tác với thiết kế của chính họ
    public boolean isUserAuthorized(Long userId, String username) {
        com.techbytedev.signboardmanager.entity.User user = userService.findByUsername(username);
        return user != null && user.getId().equals(userId);
    }

    // Lấy userId từ username
    public Long getUserIdByUsername(String username) {
        com.techbytedev.signboardmanager.entity.User user = userService.findByUsername(username);
        return user != null ? user.getId() != null ? user.getId().longValue() : null : null;
    }


    
}