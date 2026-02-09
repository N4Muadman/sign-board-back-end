package com.techbytedev.signboardmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techbytedev.signboardmanager.entity.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
}