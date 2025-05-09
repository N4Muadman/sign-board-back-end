package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.response.ResultPaginationDTO;
import com.techbytedev.signboardmanager.entity.Role;
import com.techbytedev.signboardmanager.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findById(Integer id) {
        return roleRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO findAll(Specification<Role> spec, Pageable pageable) {
        Page<Role> page = roleRepository.findAll(spec, pageable); // Sửa: spec trước, pageable sau
        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setContent(page.getContent());
        result.setPage(page.getNumber());
        result.setSize(page.getSize());
        result.setTotalElements(page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        return result;
    }

    public Role create(Role role) {
        return roleRepository.save(role);
    }

    public Role update(Role role) {
        return roleRepository.save(role);
    }

    public void delete(Integer id) {
        roleRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}