package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.UserCreateRequest;
import com.techbytedev.signboardmanager.dto.request.UserUpdateRequest;
import com.techbytedev.signboardmanager.dto.response.UserResponse;
import com.techbytedev.signboardmanager.entity.Role;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.repository.RoleRepository;
import com.techbytedev.signboardmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        logger.debug("Fetching users with pageable: {}", pageable);
        Page<User> users = userRepository.findAllByDeletedAtIsNull(pageable);
        logger.debug("Found {} users", users.getTotalElements());
        return users.map(this::convertToResponse);
    }

    public UserResponse getUserById(Integer id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return convertToResponse(user);
    }

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
        Role role = request.getRoleName().equals("Admin") ? roleRepository.findByName("Admin")
                .orElseThrow(() -> new IllegalArgumentException("Admin role not found")) : roleRepository.findByName("Customer")
                .orElseThrow(() -> new IllegalArgumentException("Customer role not found"));
        user.setRole(role);
        userRepository.save(user);

        return convertToResponse(user);
    }

    public void deleteUser(Integer id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public UserResponse assignAdminRole(Integer id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        Role adminRole = roleRepository.findByName("Admin")
                .orElseThrow(() -> new IllegalArgumentException("Admin role not found"));

        user.setRole(adminRole);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return convertToResponse(user);
    }

    public UserResponse removeAdminRole(Integer id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (!user.getRoleName().equals("Admin")) {
            throw new IllegalArgumentException("User is not an admin");
        }

        Role customerRole = roleRepository.findByName("Customer")
                .orElseThrow(() -> new IllegalArgumentException("Customer role not found"));

        user.setRole(customerRole);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return convertToResponse(user);
    }

    public UserResponse processGoogleUser(String email, String fullName) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(email);
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode("google-auth-" + email));
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            Role customerRole = roleRepository.findByName("Customer")
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
            user.setUsername(email);
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode("google-auth-" + email));
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            Role customerRole = roleRepository.findByName("Customer")
                    .orElseThrow(() -> new IllegalArgumentException("Customer role not found"));
            user.setRole(customerRole);

            userRepository.save(user);
        }

        return user;
    }

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
       
        Role role = request.getRoleName().equals("Admin") ? roleRepository.findByName("Admin")
                .orElseThrow(() -> new IllegalArgumentException("Admin role not found")) : roleRepository.findByName("Customer")
                .orElseThrow(() -> new IllegalArgumentException("Customer role not found"));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(true);
        userRepository.save(user);
        UserResponse response = convertToResponse(user);

        return response;
    }

    public Page<UserResponse> searchUsers(String username, String email, String roleName, Boolean isActive, Pageable pageable) {
        return userRepository.searchUsers(username, email, roleName, isActive, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        logger.debug("Found user: username={}", username);
        return user;
    }

    public User findById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        logger.debug("Found user: id={}", userId);
        return user;
    }

    public UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setActive(user.isActive());
        response.setRoleName(user.getRoleName());
        return response;
    }
}