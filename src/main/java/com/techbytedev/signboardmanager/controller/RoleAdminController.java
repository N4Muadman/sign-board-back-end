package com.techbytedev.signboardmanager.controller;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.turkraft.springfilter.boot.Filter;


import com.techbytedev.signboardmanager.dto.response.ResultPaginationDTO;
import com.techbytedev.signboardmanager.entity.Role;
import com.techbytedev.signboardmanager.exception.InvalidException;
import com.techbytedev.signboardmanager.service.RoleService;

@RestController
@RequestMapping("/api/admin")
public class RoleAdminController {

    private final RoleService roleService;

    public RoleAdminController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> create(@RequestBody @Valid Role role) throws InvalidException {
        if (roleService.existsByName(role.getName())) {
            throw new InvalidException("Vai trò với tên '" + role.getName() + "' đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(role));
    }

    @PutMapping("/roles")
    public ResponseEntity<Role> update(@RequestBody @Valid Role role) throws InvalidException {
        if (roleService.findById(role.getId()) == null) {
            throw new InvalidException("Vai trò với ID " + role.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(roleService.update(role));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) throws InvalidException {
        if (roleService.findById(id) == null) {
            throw new InvalidException("Vai trò với ID " + id + " không tồn tại");
        }
        roleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles")
    public ResponseEntity<ResultPaginationDTO> fetchAll(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(roleService.findAll(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> fetchById(@PathVariable("id") Integer id) throws InvalidException {
        Role role = roleService.findById(id);
        if (role == null) {
            throw new InvalidException("Vai trò với ID " + id + " không tồn tại");
        }
        return ResponseEntity.ok(role);
    }
}