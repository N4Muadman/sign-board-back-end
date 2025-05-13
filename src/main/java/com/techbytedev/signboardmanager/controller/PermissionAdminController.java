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

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class PermissionAdminController {

    private final PermissionService permissionService;

    public PermissionAdminController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/permissions/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/permissions/**', 'GET')")
    public ResponseEntity<Permission> layTheoId(@PathVariable("id") Long id) throws InvalidException {
        Permission permission = permissionService.findById(id);
        if (permission == null) {
            throw new InvalidException("Quyền với ID " + id + " không tồn tại");
        }
        return ResponseEntity.ok(permission);
    }

    @GetMapping("/permissions")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/permissions/**', 'GET')")
    public ResponseEntity<ResultPaginationDTO> layTatCa(
            @RequestParam(required = false) String ten,
            @RequestParam(required = false) String duongDanApi,
            @RequestParam(required = false) String phuongThuc,
            @RequestParam(required = false) String module,
            Pageable pageable) {
        return ResponseEntity.ok(permissionService.findAll(ten, duongDanApi, phuongThuc, module, pageable));
    }

    @PostMapping("/permissions")
    @PreAuthorize("@permissionRequester.hasPermission(authentication, '/api/admin/permissions/**', 'POST')")
    public ResponseEntity<Permission> tao(@RequestBody @Valid Permission permission) throws InvalidException {
        if (permissionService.exists(permission)) {
            throw new InvalidException("Quyền đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(permission));
    }

    @PutMapping("/permissions")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/permissions/**', 'PUT')")
    public ResponseEntity<Permission> capNhat(@RequestBody @Valid Permission permission) throws InvalidException {
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
    public ResponseEntity<Void> xoa(@PathVariable("id") Long id) throws InvalidException {
        if (permissionService.findById(id) == null) {
            throw new InvalidException("Quyền với ID " + id + " không tồn tại");
        }
        permissionService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/permissions/assign")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/permissions/**', 'POST')")
    public ResponseEntity<Void> ganNhieuQuyenChoVaiTro(@RequestBody @Valid RolePermissionsRequest request) {
        permissionService.assignPermissionsToRole(request.roleId(), request.permissionIds());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/permissions/revoke")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/permissions/**', 'DELETE')")
    public ResponseEntity<Void> goNhieuQuyenTuVaiTro(@RequestBody @Valid RolePermissionsRequest request) {
        permissionService.revokePermissionsFromRole(request.roleId(), request.permissionIds());
        return ResponseEntity.ok().build();
    }
}

record RolePermissionsRequest(Long roleId, List<Long> permissionIds) {}