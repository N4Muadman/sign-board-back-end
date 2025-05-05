package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.UserUpdateRequest;
import com.techbytedev.signboardmanager.dto.response.UserResponse;
import com.techbytedev.signboardmanager.entity.Role;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.repository.RoleRepository;
import com.techbytedev.signboardmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Lấy danh sách người dùng chưa bị xóa
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllByDeletedAtIsNull()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    // Lấy chi tiết người dùng theo id
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return convertToResponse(user);
    }

    // Cập nhật thông tin người dùng
    public UserResponse updateUser(Integer id, UserUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return convertToResponse(user);
    }

    // Xóa người dùng (soft delete)
    public void deleteUser(Integer id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Gán vai trò admin cho người dùng
    public UserResponse assignAdminRole(Integer id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        Role adminRole = roleRepository.findByName("admin")
            .orElseThrow(() -> new IllegalArgumentException("Admin role not found"));

        user.setRole(adminRole);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return convertToResponse(user);
    }

    // Gỡ vai trò admin và gán lại vai trò customer
    public UserResponse removeAdminRole(Integer id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (!user.getRole().getName().equals("admin")) {
            throw new IllegalArgumentException("User is not an admin");
        }

        Role customerRole = roleRepository.findByName("customer")
            .orElseThrow(() -> new IllegalArgumentException("Customer role not found"));

        user.setRole(customerRole);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return convertToResponse(user);
    }

    // Xử lý đăng ký/đăng nhập với Google
    public UserResponse processGoogleUser(String email, String fullName) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Đăng ký người dùng mới
            user = new User();
            user.setEmail(email);
            user.setUsername(email); // Sử dụng email làm username
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode("google-auth-" + email)); // Mật khẩu giả để tránh lỗi
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            Role customerRole = roleRepository.findByName("customer")
                .orElseThrow(() -> new IllegalArgumentException("Customer role not found"));
            user.setRole(customerRole);

            userRepository.save(user);
        }

        return convertToResponse(user);
    }

    public User findOrCreateUser(String email, String fullName) {
        User user = userRepository.findByEmail(email).orElse(null);
    
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(email); // Sử dụng email làm username
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode("google-auth-" + email)); // Mật khẩu giả
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
    
            Role customerRole = roleRepository.findByName("customer")
                .orElseThrow(() -> new IllegalArgumentException("Customer role not found"));
            user.setRole(customerRole);
    
            userRepository.save(user);
        }
    
        return user;
    }

    // Phương thức mới: Lọc và tìm kiếm người dùng
    public List<UserResponse> searchUsers(String username, String email, String roleName, Boolean isActive) {
        return userRepository.searchUsers(username, email, roleName, isActive)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    // Chuyển đổi User entity sang UserResponse
    public UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setActive(user.isActive());
        response.setRoleName(user.getRole().getName());
        return response;
    }
}