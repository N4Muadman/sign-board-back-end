package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.UserDesign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDesignRepository extends JpaRepository<UserDesign, Integer> {
    List<UserDesign> findByUserIdAndDeletedAtIsNull(Integer userId);
    List<UserDesign> findByStatusAndDeletedAtIsNull(UserDesign.Status status);
}