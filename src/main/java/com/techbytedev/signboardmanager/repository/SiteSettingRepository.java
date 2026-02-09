package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.SiteSetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteSettingRepository extends JpaRepository<SiteSetting, Integer> {
    Page<SiteSetting> findAll(Pageable pageable);
}
