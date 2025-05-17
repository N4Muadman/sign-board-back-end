package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.username = :username AND u.deletedAt IS NULL")
    Optional<User> findByUsername(@Param("username") String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    Page<User> findAllByDeletedAtIsNull(Pageable pageable);
    
    Optional<User> findByIdAndDeletedAtIsNull(Integer id);

    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL " +
           "AND (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))) " +
           "AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
           "AND (:roleName IS NULL OR u.role.name = :roleName) " +
           "AND (:isActive IS NULL OR u.isActive = :isActive)")
    Page<User> searchUsers(
            @Param("username") String username,
            @Param("email") String email,
            @Param("roleName") String roleName,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    @Query("SELECT u.role.id FROM User u WHERE u.id = :userId")
    Integer findRoleIdByUserId(@Param("userId") Integer userId);
}