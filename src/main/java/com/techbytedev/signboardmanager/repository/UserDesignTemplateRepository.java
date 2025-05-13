package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.UserDesignTemplate;
import com.techbytedev.signboardmanager.entity.UserDesignTemplateId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDesignTemplateRepository extends JpaRepository<UserDesignTemplate, UserDesignTemplateId> {
}