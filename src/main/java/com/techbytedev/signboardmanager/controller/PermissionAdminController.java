package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.response.ResultPaginationDTO;
import com.techbytedev.signboardmanager.entity.Permission;
import com.techbytedev.signboardmanager.exception.InvalidException;
import com.techbytedev.signboardmanager.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class PermissionAdminController {

    private final PermissionService permissionService;

    public PermissionAdminController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/permissions/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/permissions/**', 'GET')")
    public ResponseEntity<Permission> fetchById(@PathVariable("id") Long id) throws InvalidException {
        Permission permission = permissionService.findById(id);
        if (permission == null) {
            throw new InvalidException("Quyền với ID " + id + " không tồn tại");
        }
        return ResponseEntity.ok(permission);
    }

    @GetMapping("/permissions")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/permissions/**', 'GET')")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String apiPath,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String module,
            Pageable pageable) {
        return ResponseEntity.ok(permissionService.findAll(name, apiPath, method, module, pageable));
    }

    @PostMapping("/permissions")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/permissions/**', 'POST')")
    public ResponseEntity<Permission> create(@RequestBody @Valid Permission permission) throws InvalidException {
        if (permissionService.exists(permission)) {
            throw new InvalidException("Quyền đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(permission));
    }

    @PutMapping("/permissions")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/permissions/**', 'PUT')")
    public ResponseEntity<Permission> update(@RequestBody @Valid Permission permission) throws InvalidException {
        if (permissionService.findById(permission.getId()) == null) {
            throw new InvalidException("Quyền với ID " + permission.getId() + " không tồn tại");
        }
        if (permissionService.exists(permission)) {
            throw new IllegalStateException("Quyền đã tồn tại");
        }
        return ResponseEntity.ok(permissionService.update(permission));
    }

    @DeleteMapping("/permissions/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/permissions/**', 'DELETE')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws InvalidException {
        if (permissionService.findById(id) == null) {
            throw new InvalidException("Quyền với ID " + id + " không tồn tại");
        }
        permissionService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/permissions/assign")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/permissions/**', 'POST')")
    public ResponseEntity<Void> assignPermissionToRole(@RequestBody RolePermissionRequest request) {
        permissionService.assignPermissionToRole(request.roleId(), request.permissionId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/permissions/revoke")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/permissions/**', 'DELETE')")
    public ResponseEntity<Void> revokePermissionFromRole(@RequestBody RolePermissionRequest request) {
        permissionService.revokePermissionFromRole(request.roleId(), request.permissionId());
        return ResponseEntity.ok().build();
    }
}

record RolePermissionRequest(Long roleId, Long permissionId) {}