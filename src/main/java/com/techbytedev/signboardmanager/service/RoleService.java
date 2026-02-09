package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.RoleCreateDTO;
import com.techbytedev.signboardmanager.dto.request.RoleUpdateDTO;
import com.techbytedev.signboardmanager.dto.response.PermissionResponseDTO;
import com.techbytedev.signboardmanager.dto.response.RoleResponseDTO;
import com.techbytedev.signboardmanager.entity.Permission;
import com.techbytedev.signboardmanager.entity.Role;
import com.techbytedev.signboardmanager.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionService permissionService;

    @Transactional(readOnly = true)
    public Page<RoleResponseDTO> fetchAllRolesWithPermissions(Pageable pageable) {
        try {
            Page<Role> rolePage = roleRepository.findAll(pageable);
            List<RoleResponseDTO> roleDTOs = rolePage.getContent().stream()
                    .map(this::convertToRoleResponseDTO)
                    .collect(Collectors.toList());
            log.info("Lấy được {} vai trò trong trang {}", roleDTOs.size(), pageable.getPageNumber());
            return new PageImpl<>(roleDTOs, pageable, rolePage.getTotalElements());
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách vai trò phân trang: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể lấy danh sách vai trò: " + e.getMessage(), e);
        }
    }

    // Các phương thức khác giữ nguyên
    @Transactional(readOnly = true)
    public RoleResponseDTO fetchById(Integer id) {
        try {
            Role role = roleRepository.findByIdWithPermissions(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với id: " + id));
            log.info("Lấy được vai trò với id: {}", id);
            return convertToRoleResponseDTO(role);
        } catch (Exception e) {
            log.error("Lỗi khi lấy vai trò với id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Không thể lấy vai trò với id: " + id + ", lỗi: " + e.getMessage(), e);
        }
    }

    @Transactional
    public RoleResponseDTO create(RoleCreateDTO request) {
        try {
            Role role = new Role();
            role.setName(request.getName());
            role.setDescription(request.getDescription());
            role.setActive(true);
            role = roleRepository.save(role);
            log.info("Tạo vai trò: {}", role.getName());
            return convertToRoleResponseDTO(role);
        } catch (Exception e) {
            log.error("Lỗi khi tạo vai trò {}: {}", request.getName(), e.getMessage(), e);
            throw new RuntimeException("Không thể tạo vai trò: " + e.getMessage(), e);
        }
    }

    @Transactional
    public RoleResponseDTO update(Integer id, RoleUpdateDTO request) {
        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với id: " + id));
            role.setName(request.getName());
            role.setDescription(request.getDescription());
            role.setActive(request.getActive());
            role = roleRepository.save(role);
            log.info("Cập nhật vai trò với id: {}", id);
            return convertToRoleResponseDTO(role);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật vai trò với id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Không thể cập nhật vai trò: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void delete(Integer id) {
        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với id: " + id));
            roleRepository.delete(role);
            log.info("Xóa vai trò với id: {}", id);
        } catch (Exception e) {
            log.error("Lỗi khi xóa vai trò với id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Không thể xóa vai trò: " + e.getMessage(), e);
        }
    }

    private RoleResponseDTO convertToRoleResponseDTO(Role role) {
        try {
            RoleResponseDTO dto = new RoleResponseDTO();
            dto.setId(role.getId());
            dto.setName(role.getName());
            dto.setDescription(role.getDescription());
            dto.setActive(role.getActive());
            List<Permission> permissions = permissionService.findByRoleId(role.getId());
            dto.setPermissions(permissions.stream()
                    .map(this::convertToPermissionResponseDTO)
                    .collect(Collectors.toSet()));
            return dto;
        } catch (Exception e) {
            log.error("Lỗi khi chuyển đổi Role sang DTO: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể chuyển đổi Role sang DTO: " + e.getMessage(), e);
        }
    }

    private PermissionResponseDTO convertToPermissionResponseDTO(Permission permission) {
        try {
            PermissionResponseDTO dto = new PermissionResponseDTO();
            dto.setId(permission.getId());
            dto.setName(permission.getName());
            dto.setApiPath(permission.getApiPath());
            dto.setMethod(permission.getMethod());
            dto.setModule(permission.getModule());
            return dto;
        } catch (Exception e) {
            log.error("Lỗi khi chuyển đổi Permission sang DTO: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể chuyển đổi Permission sang DTO: " + e.getMessage(), e);
        }
    }
}