package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Material;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {
    List<Material> findByNameContainingIgnoreCase(String name);
    Page<Material> findAll(Pageable pageable);
}
