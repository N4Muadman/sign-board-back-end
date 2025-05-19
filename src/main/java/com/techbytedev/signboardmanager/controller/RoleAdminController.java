package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.request.RoleCreateDTO;
import com.techbytedev.signboardmanager.dto.request.RoleUpdateDTO;
import com.techbytedev.signboardmanager.dto.response.RoleResponseDTO;
import com.techbytedev.signboardmanager.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
public class RoleAdminController {

    private static final Logger log = LoggerFactory.getLogger(RoleAdminController.class);

    private final RoleService roleService;

    public RoleAdminController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/roles', 'GET')")
    public ResponseEntity<?> fetchAll() {
        try {
            List<RoleResponseDTO> roles = roleService.fetchAllRolesWithPermissions();
            log.info("Trả về {} vai trò", roles.size());
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách vai trò: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Không thể lấy danh sách vai trò: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/roles/**', 'GET')")
    public ResponseEntity<?> fetchById(@PathVariable Integer id) {
        try {
            RoleResponseDTO role = roleService.fetchById(id);
            log.info("Trả về vai trò với id: {}", id);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            log.error("Lỗi khi lấy vai trò với id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Không thể lấy vai trò với id: " + id + ", lỗi: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/roles', 'POST')")
    public ResponseEntity<?> create(@RequestBody RoleCreateDTO request) {
        try {
            RoleResponseDTO response = roleService.create(request);
            log.info("Tạo vai trò: {}", response.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Lỗi khi tạo vai trò: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Không thể tạo vai trò: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/roles/**', 'PUT')")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody RoleUpdateDTO request) {
        try {
            RoleResponseDTO response = roleService.update(id, request);
            log.info("Cập nhật vai trò với id: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật vai trò với id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Không thể cập nhật vai trò với id: " + id + ", lỗi: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/roles/**', 'DELETE')")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            roleService.delete(id);
            log.info("Xóa vai trò với id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Lỗi khi xóa vai trò với id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Không thể xóa vai trò với id: " + id + ", lỗi: " + e.getMessage()));
        }
    }
}