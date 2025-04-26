package com.techbytedev.signboardmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techbytedev.signboardmanager.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}