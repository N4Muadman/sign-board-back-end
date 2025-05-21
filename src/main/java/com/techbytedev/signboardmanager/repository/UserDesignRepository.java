package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.UserDesign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDesignRepository extends JpaRepository<UserDesign, Long> {
    Page<UserDesign> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<UserDesign> findByIdAndDeletedAtIsNull(Long id);
    @Modifying
    @Query("DELETE FROM UserDesignTemplate udt WHERE udt.userDesignId = :designId")
    void deleteByUserDesignId(@Param("designId") Long designId);
}