package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    @Query("SELECT p FROM Permission p JOIN p.roles r " +
           "WHERE r.name = :roleName " +
           "AND (p.apiPath = :apiPath " +
           "OR p.apiPath = REPLACE(:apiPath, '/{id}', '/[0-9]+') " +
           "OR p.apiPath = REPLACE(:apiPath, '/{id}', '/:id') " +
           "OR (p.apiPath LIKE '%[**]' AND :apiPath LIKE REPLACE(p.apiPath, '**', '%'))) " +
           "AND (p.method = :method OR p.method = 'ALL')")
    List<Permission> findByRoleNameAndApiPathAndMethod(
            @Param("roleName") String roleName,
            @Param("apiPath") String apiPath,
            @Param("method") String method
    );

    boolean existsByNameAndApiPathAndMethodAndModule(String name, String apiPath, String method, String module);
}