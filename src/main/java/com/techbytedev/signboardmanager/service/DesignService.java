package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.DesignTemplate;
import com.techbytedev.signboardmanager.entity.User;
import com.techbytedev.signboardmanager.entity.UserDesign;
import com.techbytedev.signboardmanager.repository.DesignTemplateRepository;
import com.techbytedev.signboardmanager.repository.UserDesignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DesignService {

    private static final Logger logger = LoggerFactory.getLogger(DesignService.class);
    private static final String UPLOAD_DIR = "uploads/";

    private final UserDesignRepository userDesignRepository;
    private final DesignTemplateRepository designTemplateRepository;
    private final UserService userService;

    public DesignService(UserDesignRepository userDesignRepository, DesignTemplateRepository designTemplateRepository, UserService userService) {
        this.userDesignRepository = userDesignRepository;
        this.designTemplateRepository = designTemplateRepository;
        this.userService = userService;
    }

    // public List<DesignTemplate> getTemplates(String category) {
    //     return designTemplateRepository.findByCategoryAndIsActiveTrueAndDeletedAtIsNull(category);
    // }

   
    @Transactional
    public String saveImageDesign(Integer userId, MultipartFile image) throws IOException {
        logger.info("Saving image design for user {}", userId);
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = userId + "_" + System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, image.getBytes());

        return "uploads/" + fileName;
    }



   
}