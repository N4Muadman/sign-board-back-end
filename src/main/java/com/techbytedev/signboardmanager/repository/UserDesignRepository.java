package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.UserDesign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserDesignRepository extends JpaRepository<UserDesign, Integer> {
    @Query("SELECT ud FROM UserDesign ud WHERE ud.user.id = :userId AND ud.deletedAt IS NULL")
    List<UserDesign> findByUser_IdAndDeletedAtIsNull(@Param("userId") Integer userId);

 

    List<UserDesign> findAllByDeletedAtIsNull();
    Optional<UserDesign> findByIdAndDeletedAtIsNull(Long id);
}