package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.Material;
import com.techbytedev.signboardmanager.repository.MaterialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }
    public Page<Material> getAllMaterials(Pageable pageable) {
        return materialRepository.findAll(pageable);
    }
    public Material createMaterial(Material material) {
        return materialRepository.save(material);
    }
    public Material updateMaterial(int id, Material updated) {
        Material existing = materialRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setUpdatedAt(LocalDateTime.now());
        return materialRepository.save(existing);
    }

    public void deleteMaterial(int id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        material.setDeletedAt(LocalDateTime.now());
        materialRepository.save(material);
    }

    public List<Material> searchMaterialsByName(String name) {
        return materialRepository.findByNameContainingIgnoreCase(name);
    }
}
