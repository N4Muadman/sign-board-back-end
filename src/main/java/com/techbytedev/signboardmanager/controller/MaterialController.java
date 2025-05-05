package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.entity.Material;
import com.techbytedev.signboardmanager.service.MaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {
    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }
    // hiển thị danh sách
    @GetMapping("/list")
    public ResponseEntity<List<Material>> getAllMaterials() {
        List<Material> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(materials);
    }
    // thêm
    @PostMapping("/create")
    public ResponseEntity<?> createMaterial(@RequestBody Material material) {
        try {
            Material saved = materialService.createMaterial(material);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
    // sửa
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> updateMaterial(@PathVariable int id, @RequestBody Material updatedMaterial) {
        try {
            Material updated = materialService.updateMaterial(id, updatedMaterial);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy vật liệu");
        }
    }
    // xóa
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMaterial(@PathVariable int id) {
        try {
            materialService.deleteMaterial(id);
            return ResponseEntity.ok("Xóa vật liệu thành công");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy vật liệu");
        }
    }

    // tìm kiếm theo tên
    @GetMapping("/search")
    public ResponseEntity<List<Material>> searchMaterials(@RequestParam String name) {
        return ResponseEntity.ok(materialService.searchMaterialsByName(name));
    }
}
