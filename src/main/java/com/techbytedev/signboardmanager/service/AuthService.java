package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.AuthRequest;
import com.techbytedev.signboardmanager.dto.request.RegisterRequest;
import com.techbytedev.signboardmanager.dto.response.AuthResponse;
import com.techbytedev.signboardmanager.entity.Role;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.repository.RoleRepository;
import com.techbytedev.signboardmanager.repository.UserRepository;
import com.techbytedev.signboardmanager.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                      AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
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

        Role role = roleRepository.findByName("customer");
        if (role == null) {
            role = new Role();
            role.setName("customer");
            roleRepository.save(role);
        }
        user.setRole(role);

        userRepository.save(user);

        String jwt = jwtUtil.generateToken(user);
        return new AuthResponse(jwt);
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(user);
        return new AuthResponse(jwt);
    }
}