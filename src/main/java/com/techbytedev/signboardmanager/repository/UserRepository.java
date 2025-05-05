package com.techbytedev.signboardmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Truy vấn tùy chỉnh để lọc và tìm kiếm người dùng
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL " +
           "AND (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) " +
           "AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
           "AND (:roleName IS NULL OR u.role.name = :roleName) " +
           "AND (:isActive IS NULL OR u.isActive = :isActive)")
    List<User> searchUsers(
        @Param("username") String username,
        @Param("email") String email,
        @Param("roleName") String roleName,
        @Param("isActive") Boolean isActive
    );
}