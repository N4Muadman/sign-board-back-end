package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.UserDesignTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDesignTemplateRepository extends JpaRepository<UserDesignTemplate, Long> {
    void deleteByUserDesignIdAndDesignTemplateId(Long userDesignId, Long designTemplateId);
}