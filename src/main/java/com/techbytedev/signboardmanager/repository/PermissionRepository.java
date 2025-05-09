package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    boolean existsByNameAndApiPathAndMethodAndModule(String name, String apiPath, String method, String module);
}