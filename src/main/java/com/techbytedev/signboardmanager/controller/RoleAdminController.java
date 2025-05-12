package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.request.RoleCreateDTO;
import com.techbytedev.signboardmanager.dto.request.RoleFilterDTO;
import com.techbytedev.signboardmanager.dto.request.RoleUpdateDTO;
import com.techbytedev.signboardmanager.dto.response.ResultPaginationDTO;
import com.techbytedev.signboardmanager.dto.response.RoleResponseDTO;
import com.techbytedev.signboardmanager.entity.Role;
import com.techbytedev.signboardmanager.service.RoleService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
import jakarta.persistence.criteria.Predicate;

@RestController
@RequestMapping("/api/admin/roles")
public class RoleAdminController {

    private final RoleService roleService;

    public RoleAdminController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            RoleFilterDTO filter,
            Pageable pageable
    ) {
        Specification<Role> spec = buildSpecification(filter);
        ResultPaginationDTO result = roleService.fetchAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<RoleResponseDTO> create(@RequestBody RoleCreateDTO request) {
        RoleResponseDTO response = roleService.create(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody RoleUpdateDTO request
    ) {
        RoleResponseDTO response = roleService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Specification<Role> buildSpecification(RoleFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            if (filter == null) {
                return null;
            }

            var predicates = Optional.ofNullable(filter)
                    .map(f -> {
                        var p = new ArrayList<Predicate>();
                        if (f.getName() != null && !f.getName().isEmpty()) {
                            p.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + f.getName().toLowerCase() + "%"));
                        }
                        if (f.getActive() != null) {
                            p.add(criteriaBuilder.equal(root.get("active"), f.getActive()));
                        }
                        if (f.getDescription() != null && !f.getDescription().isEmpty()) {
                            p.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + f.getDescription().toLowerCase() + "%"));
                        }
                        return p;
                    })
                    .orElse(new ArrayList<Predicate>());

            return predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}