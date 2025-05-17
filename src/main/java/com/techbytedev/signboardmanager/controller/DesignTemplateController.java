package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.entity.DesignTemplate;
import com.techbytedev.signboardmanager.service.DesignTemplateService;
import com.techbytedev.signboardmanager.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/design-templates")
public class DesignTemplateController {

    private static final Logger logger = LoggerFactory.getLogger(DesignTemplateController.class);
    private final DesignTemplateService designTemplateService;
    private final FileStorageService fileStorageService;

    public DesignTemplateController(DesignTemplateService designTemplateService, FileStorageService fileStorageService) {
        this.designTemplateService = designTemplateService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<DesignTemplate> taoMauThietKe(
            @RequestParam(value = "previewImageUrl", required = false) MultipartFile previewImage,
            @RequestParam("canvasTemplateLink") String canvasTemplateLink) throws IOException {
        logger.debug("Creating design template with canvasTemplateLink: {}", canvasTemplateLink);
        DesignTemplate designTemplate = new DesignTemplate();
        designTemplate.setCanvasTemplateLink(canvasTemplateLink);

        if (previewImage != null && !previewImage.isEmpty()) {
            String filePath = fileStorageService.saveFile(previewImage);
            designTemplate.setPreviewImageUrl(filePath);
            logger.info("Saved preview image at: {}", filePath);
        }

        DesignTemplate savedTemplate = designTemplateService.tao(designTemplate);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTemplate);
    }

    @GetMapping
    public ResponseEntity<List<DesignTemplate>> layTatCaMauThietKe() {
        logger.debug("Fetching all design templates");
        return ResponseEntity.ok(designTemplateService.layTatCa());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignTemplate> layMauThietKeTheoId(@PathVariable("id") Long id) {
        logger.debug("Fetching design template with id: {}", id);
        DesignTemplate designTemplate = designTemplateService.layTheoId(id);
        return designTemplate != null ? ResponseEntity.ok(designTemplate) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesignTemplate> capNhatMauThietKe(
            @PathVariable("id") Long id,
            @RequestParam(value = "previewImageUrl", required = false) MultipartFile previewImage,
            @RequestParam(value = "canvasTemplateLink", required = false) String canvasTemplateLink) throws IOException {
        logger.debug("Updating design template with id: {}", id);
        DesignTemplate designTemplate = designTemplateService.layTheoId(id);
        if (designTemplate == null) {
            logger.warn("Design template with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (previewImage != null && !previewImage.isEmpty()) {
            String filePath = fileStorageService.saveFile(previewImage);
            designTemplate.setPreviewImageUrl(filePath);
            logger.info("Updated preview image at: {}", filePath);
        }
        if (canvasTemplateLink != null) {
            designTemplate.setCanvasTemplateLink(canvasTemplateLink);
        }

        DesignTemplate updatedTemplate = designTemplateService.capNhat(designTemplate);
        return ResponseEntity.ok(updatedTemplate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaMauThietKe(@PathVariable("id") Long id) {
        logger.debug("Deleting design template with id: {}", id);
        if (designTemplateService.xoa(id)) {
            return ResponseEntity.ok().build();
        }
        logger.warn("Design template with id {} not found for deletion", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}