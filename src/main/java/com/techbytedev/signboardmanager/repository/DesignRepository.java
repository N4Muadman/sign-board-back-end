package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Design;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DesignRepository extends JpaRepository<Design, Long> {
    Optional<Design> findByCanvaDesignId(String canvaDesignId);
}