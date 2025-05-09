package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.response.ResultPaginationDTO;
import com.techbytedev.signboardmanager.entity.Permission;
import com.techbytedev.signboardmanager.entity.Role;
import com.techbytedev.signboardmanager.repository.PermissionRepository;
import com.techbytedev.signboardmanager.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionSpecification permissionSpecification;
    @PersistenceContext
    private EntityManager entityManager;

    public PermissionService(PermissionRepository permissionRepository,
                             RoleRepository roleRepository,
                             PermissionSpecification permissionSpecification) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.permissionSpecification = permissionSpecification;
    }

    public Permission findById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO findAll(String name, String apiPath, String method, String module, Pageable pageable) {
        Specification<Permission> spec = permissionSpecification.buildSpecification(name, apiPath, method, module);
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

    @Transactional(readOnly = true)
    public boolean hasPermission(String roleName, String apiPath, String method) {
        List<Permission> permissions = permissionRepository.findByRoleNameAndApiPathAndMethod(roleName, apiPath, method);
        return !permissions.isEmpty();
    }

    @Transactional
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Vai trò với ID " + roleId + " không tồn tại"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Quyền với ID " + permissionId + " không tồn tại"));

        Query query = entityManager.createNativeQuery(
                "INSERT INTO role_permissions (role_id, permission_id) VALUES (:roleId, :permissionId)");
        query.setParameter("roleId", roleId.intValue());
        query.setParameter("permissionId", permissionId);
        query.executeUpdate();
    }

    @Transactional
    public void revokePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Vai trò với ID " + roleId + " không tồn tại"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Quyền với ID " + permissionId + " không tồn tại"));

        Query query = entityManager.createNativeQuery(
                "DELETE FROM role_permissions WHERE role_id = :roleId AND permission_id = :permissionId");
        query.setParameter("roleId", roleId.intValue());
        query.setParameter("permissionId", permissionId);
        int rowsAffected = query.executeUpdate();
        if (rowsAffected == 0) {
            throw new IllegalStateException("Quyền không được gán cho vai trò này");
        }
    }
}