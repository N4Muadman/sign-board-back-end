package com.techbytedev.signboardmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techbytedev.signboardmanager.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findAllByDeletedAtIsNull(); // Lấy danh sách người dùng chưa bị xóa
    Optional<User> findByIdAndDeletedAtIsNull(Integer id); // Lấy người dùng theo id, chưa bị xóa
}