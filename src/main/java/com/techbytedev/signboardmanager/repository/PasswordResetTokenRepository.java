package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUserId(Integer userId); // Thay Long thành Integer
}