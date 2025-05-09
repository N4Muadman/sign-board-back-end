package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.response.ResultPaginationDTO;
import com.techbytedev.signboardmanager.entity.Permission;
import com.techbytedev.signboardmanager.entity.Role;
import com.techbytedev.signboardmanager.repository.PermissionRepository;
import com.techbytedev.signboardmanager.repository.RoleRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class PermissionService  {

    private final PermissionRepository permissionRepository;
        private final RoleRepository roleRepository;


    public PermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    public Permission findById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO findAll(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> page = permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setContent(page.getContent());
        result.setPage(page.getNumber());
        result.setSize(page.getSize());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        return result;
    }

    public Permission create(Permission permission) {
        return permissionRepository.save(permission);
    }

    public Permission update(Permission permission) {
        return permissionRepository.save(permission);
    }

    public void delete(Long id) {
        permissionRepository.deleteById(id);
    }

    public boolean exists(Permission permission) {
        return permissionRepository.existsByNameAndApiPathAndMethodAndModule(
            permission.getName(),
            permission.getApiPath(),
            permission.getMethod(),
            permission.getModule()
        );
    }

    public boolean hasPermission(String roleName, String apiPath, String method) {
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) {
            return false;
        }
        return role.getPermissions().stream()
                .anyMatch(permission -> permission.getApiPath().equals(apiPath) && permission.getMethod().equals(method));
    }
}