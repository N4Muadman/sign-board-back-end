package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.RoleCreateDTO;
import com.techbytedev.signboardmanager.dto.request.RoleUpdateDTO;
import com.techbytedev.signboardmanager.dto.response.ResultPaginationDTO;
import com.techbytedev.signboardmanager.dto.response.RoleResponseDTO;
import com.techbytedev.signboardmanager.entity.Role;
import com.techbytedev.signboardmanager.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public ResultPaginationDTO fetchAll(Specification<Role> spec, Pageable pageable) {
        Page<Role> page = roleRepository.findAll(spec, pageable);
        return convertToResultPaginationDTO(page);
    }

    @Transactional
    public RoleResponseDTO create(RoleCreateDTO request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Role name already exists: " + request.getName());
        }

        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setActive(request.getActive() != null ? request.getActive() : true);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        Role savedRole = roleRepository.save(role);
        return convertToRoleResponseDTO(savedRole);
    }

    @Transactional
    public RoleResponseDTO update(Integer id, RoleUpdateDTO request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + id));

        if (request.getName() != null && !request.getName().equals(role.getName()) && roleRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Role name already exists: " + request.getName());
        }

        role.setName(request.getName() != null ? request.getName() : role.getName());
        role.setDescription(request.getDescription() != null ? request.getDescription() : role.getDescription());
        role.setActive(request.getActive() != null ? request.getActive() : role.getActive());
        role.setUpdatedAt(LocalDateTime.now());

        Role updatedRole = roleRepository.save(role);
        return convertToRoleResponseDTO(updatedRole);
    }

    @Transactional
    public void delete(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + id));
        roleRepository.delete(role);
    }

    private ResultPaginationDTO convertToResultPaginationDTO(Page<Role> page) {
        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setContent(page.getContent().stream()
                .map(this::convertToRoleResponseDTO)
                .collect(Collectors.toList()));
        result.setPage(page.getNumber());
        result.setSize(page.getSize());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        return result;
    }

    @Transactional(readOnly = true)
    private RoleResponseDTO convertToRoleResponseDTO(Role role) {
        RoleResponseDTO response = new RoleResponseDTO();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setActive(role.getActive());
        response.setCreatedAt(role.getCreatedAt());
        response.setUpdatedAt(role.getUpdatedAt());
        return response;
    }
}