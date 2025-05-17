package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.UserDesignTemplate;
import com.techbytedev.signboardmanager.entity.UserDesignTemplateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDesignTemplateRepository extends JpaRepository<UserDesignTemplate, UserDesignTemplateId> {
    
}