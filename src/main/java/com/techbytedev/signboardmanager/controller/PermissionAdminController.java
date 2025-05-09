package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.response.ResultPaginationDTO;
import com.techbytedev.signboardmanager.entity.Permission;
import com.techbytedev.signboardmanager.exception.InvalidException;
import com.techbytedev.signboardmanager.service.PermissionService;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class PermissionAdminController {

    private final PermissionService permissionService;

    public PermissionAdminController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/permissions/{id}")
    public ResponseEntity<Permission> fetchById(@PathVariable("id") Long id) throws InvalidException {
        Permission permission = permissionService.findById(id);
        if (permission == null) {
            throw new InvalidException("Quyền với ID " + id + " không tồn tại");
        }
        return ResponseEntity.ok(permission);
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok(permissionService.findAll(spec, pageable));
    }

    @PostMapping("/permissions")
    public ResponseEntity<Permission> create(@RequestBody @Valid Permission permission) throws InvalidException {
        if (permissionService.exists(permission)) {
            throw new InvalidException("Quyền đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(permission));
    }

    @PutMapping("/permissions")
    public ResponseEntity<Permission> update(@RequestBody @Valid Permission permission) throws InvalidException {
        if (permissionService.findById(permission.getId()) == null) {
            throw new InvalidException("Quyền với ID " + permission.getId() + " không tồn tại");
        }
        if (permissionService.exists(permission)) {
            throw new InvalidException("Quyền đã tồn tại");
        }
        return ResponseEntity.ok(permissionService.update(permission));
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws InvalidException {
        if (permissionService.findById(id) == null) {
            throw new InvalidException("Quyền với ID " + id + " không tồn tại");
        }
        permissionService.delete(id);
        return ResponseEntity.ok().build();
    }
}