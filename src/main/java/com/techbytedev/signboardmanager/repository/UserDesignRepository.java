package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.UserDesign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDesignRepository extends JpaRepository<UserDesign, Long> {
    List<UserDesign> findAllByDeletedAtIsNull();
    Optional<UserDesign> findByIdAndDeletedAtIsNull(Long id);
}