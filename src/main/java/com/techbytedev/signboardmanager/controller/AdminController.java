package com.techbytedev.signboardmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbytedev.signboardmanager.dto.request.ArticleRequest;
import com.techbytedev.signboardmanager.dto.request.InquiryRequest;
import com.techbytedev.signboardmanager.dto.request.ProductRequest;
import com.techbytedev.signboardmanager.dto.request.UserCreateRequest;
import com.techbytedev.signboardmanager.dto.request.UserUpdateRequest;
import com.techbytedev.signboardmanager.dto.response.CustomPageResponse;
import com.techbytedev.signboardmanager.dto.response.ProductResponse;
import com.techbytedev.signboardmanager.dto.response.UserDesignResponseDTO;
import com.techbytedev.signboardmanager.dto.response.UserResponse;
import com.techbytedev.signboardmanager.entity.*;
import com.techbytedev.signboardmanager.repository.UserDesignRepository;
import com.techbytedev.signboardmanager.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserDesignRepository userDesignRepository;
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ContactService contactService;
    private final ArticleService articleService;
    private final SiteSettingService siteSettingService;
    private final MaterialService materialService;
    private final InquiryService inquiryService;
    private final ObjectMapper objectMapper;
    private final UserDesignService userDesignService;

    @Autowired
    public AdminController(UserDesignRepository userDesignRepository, UserService userService, ProductService productService,
                           CategoryService categoryService, ContactService contactService, ArticleService articleService,
                           SiteSettingService siteSettingService, MaterialService materialService, InquiryService inquiryService,
                           ObjectMapper objectMapper, UserDesignService userDesignService) {
        this.userDesignRepository = userDesignRepository;
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.contactService = contactService;
        this.articleService = articleService;
        this.siteSettingService = siteSettingService;
        this.materialService = materialService;
        this.inquiryService = inquiryService;
        this.objectMapper = objectMapper;
        this.userDesignService = userDesignService;
    }

    // Xem danh sách các thiết kế
   @GetMapping("/user-designs")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/user-designs/**', 'GET')")
    public ResponseEntity<Page<UserDesignResponseDTO>> getAllUserDesigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.debug("Fetching all user designs for admin with pagination: page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDesign> designPage = userDesignService.layTatCa(pageable);

        Page<UserDesignResponseDTO> responsePage = designPage.map(userDesign -> {
            // Lấy thông tin người dùng từ userId
            User user = userService.findById(userDesign.getUserId() != null ? userDesign.getUserId().intValue() : null);
            return new UserDesignResponseDTO(
                    userDesign.getId(),
                    userDesign.getDesignImage(),
                    userDesign.getDesignLink(),
                    userDesign.getStatus(),
                    user != null ? user.getFullName() : "Unknown",
                    user != null ? user.getEmail() : "Unknown",
                    user != null ? user.getPhoneNumber() : "Unknown"
            );
        });

        return ResponseEntity.ok(responsePage);
    }

    // Xem chi tiết thiết kế và thông tin liên hệ người dùng
    @GetMapping("/user-designs/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/user-designs/**', 'GET')")
    public ResponseEntity<Map<String, Object>> getUserDesignDetails(@PathVariable Long id) {
        logger.debug("Fetching details for user design with id: {}", id);
        UserDesign userDesign = userDesignService.layTheoId(id);
        if (userDesign == null) {
            logger.warn("User design with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("design", userDesign);

        // Lấy thông tin liên hệ của người dùng
        Map<String, String> userInfo = userService.getUserContactInfo(userDesign.getUserId());
        response.put("userContactInfo", userInfo);

        return ResponseEntity.ok(response);
    }

    // Cập nhật trạng thái xử lý thiết kế
    @PutMapping("/user-designs/{id}/status")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/user-designs/**', 'PUT')")
    public ResponseEntity<UserDesign> updateDesignStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        logger.debug("Updating status for user design with id: {} to {}", id, status);
        UserDesign userDesign = userDesignService.layTheoId(id);
        if (userDesign == null) {
            logger.warn("User design with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        userDesign.setStatus(status);
        UserDesign updatedDesign = userDesignService.capNhat(userDesign);
        return ResponseEntity.ok(updatedDesign);
    }

    @DeleteMapping("/user-designs/{id}")
@PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/user-designs/**', 'DELETE')")
public ResponseEntity<String> deleteUserDesign(@PathVariable Long id) {
    logger.debug("Attempting to delete user design with id: {}", id);
    try {
        UserDesign userDesign = userDesignService.layTheoId(id);
        if (userDesign == null) {
            logger.warn("User design with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thiết kế");
        }
        userDesignService.xoa(id);
        logger.info("User design with id {} deleted successfully", id);
        return ResponseEntity.ok("Xóa thiết kế thành công");
    } catch (Exception e) {
        logger.error("Error deleting user design with id {}: {}", id, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa thiết kế");
    }
}
    // Gửi yêu cầu liên hệ (inquiry) tới người dùng sau khi xem thiết kế
    @PostMapping("/user-designs/{id}/contact")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/user-designs/**', 'POST')")
    public ResponseEntity<Inquiry> sendInquiryToUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {
        logger.debug("Sending inquiry for user design with id: {}", id);
        UserDesign userDesign = userDesignService.layTheoId(id);
        if (userDesign == null) {
            logger.warn("User design with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Lấy thông tin liên hệ người dùng
        Map<String, String> userInfo = userService.getUserContactInfo(userDesign.getUserId());
        if (userInfo == null || userInfo.isEmpty()) {
            logger.warn("User contact info not found for userId: {}", userDesign.getUserId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Tạo InquiryRequest và điền thông tin người dùng
        InquiryRequest inquiryRequest = new InquiryRequest();
        inquiryRequest.setName(userInfo.get("fullName"));
        inquiryRequest.setEmail(userInfo.get("email"));
        inquiryRequest.setPhone(userInfo.get("phoneNumber"));
        inquiryRequest.setAddress("N/A"); // Có thể mở rộng nếu cần lấy địa chỉ
        inquiryRequest.setMessage(requestBody.getOrDefault("message", "Admin has reviewed your design and would like to contact you."));
        inquiryRequest.setProductIds(id.toString()); // Liên kết với user design

        // Gửi inquiry
        Inquiry inquiry = inquiryService.createInquiry(inquiryRequest);
        logger.info("Inquiry created for user design id: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(inquiry);
    }

    // Quản lý người dùng
    @GetMapping("/users")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'GET')")
    public CustomPageResponse<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        int adjustedPage = page - 1;
        if (adjustedPage < 0) adjustedPage = 0;
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by(direction, sortParams[0]));
        Page<UserResponse> userPage = userService.getAllUsers(pageable);

        return new CustomPageResponse<>(
                userPage.getContent(),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isFirst(),
                userPage.isLast(),
                userPage.getNumberOfElements(),
                userPage.isEmpty()
        );
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'GET')")
    public UserResponse getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users/create")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'POST')")
    public UserResponse createUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/users/search")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'GET')")
    public CustomPageResponse<UserResponse> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        int adjustedPage = page - 1;
        if (adjustedPage < 0) adjustedPage = 0;
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by(direction, sortParams[0]));
        Page<UserResponse> userPage = userService.searchUsers(username, email, roleName, isActive, pageable);

        return new CustomPageResponse<>(
                userPage.getContent(),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isFirst(),
                userPage.isLast(),
                userPage.getNumberOfElements(),
                userPage.isEmpty()
        );
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'PUT')")
    public UserResponse updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'DELETE')")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/users/{id}/assign-admin")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'PUT')")
    public ResponseEntity<UserResponse> assignAdminRole(@PathVariable Integer id) {
        UserResponse updatedUser = userService.assignAdminRole(id);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/users/{id}/remove-admin")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'PUT')")
    public ResponseEntity<UserResponse> removeAdminRole(@PathVariable Integer id) {
        UserResponse updatedUser = userService.removeAdminRole(id);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/category/search")
    public ResponseEntity<List<Category>> searchCategory(@RequestParam String name) {
        List<Category> categories = categoryService.searchCategory(name);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping("/category/create")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/category/**', 'POST')")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category saveCategory = categoryService.saveCategory(category);
        return new ResponseEntity<>(saveCategory, HttpStatus.CREATED);
    }

    @PutMapping("/category/edit/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/category/**', 'PUT')")
    public Category updateCategory(@PathVariable int id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/category/delete/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/category/**', 'DELETE')")
    public ResponseEntity<String> deleteCategory(@PathVariable int id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Xóa danh mục thành công");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Không tìm thấy danh mục");
        }
    }

    @GetMapping("/contact/list")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/contact/**', 'GET')")
    public ResponseEntity<Map<String, Object>> getAllContact(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Contact> contactPage = contactService.getAllContacts(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", contactPage.getContent());
        response.put("pageNumber", contactPage.getNumber() + 1);
        response.put("pageSize", contactPage.getSize());
        response.put("totalPages", contactPage.getTotalPages());
        response.put("totalElements", contactPage.getTotalElements());
        response.put("last", contactPage.isLast());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/product/create", consumes = {"multipart/form-data"})
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/product/create', 'POST')")
    public ResponseEntity<ProductResponse> createProduct(
            @RequestPart("product") ProductRequest productRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) {
        try {
            ProductResponse productResponse = productService.createProduct(productRequest, imageFiles);
            return ResponseEntity.ok(productResponse);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping(value = "/product/edit/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/product/edit/{id}', 'PUT')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("id") int productId,
            @RequestPart("product") ProductRequest productRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) {
        try {
            ProductResponse response = productService.updateProduct(productId, productRequest, imageFiles);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/product/delete/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/product/delete/{id}', 'DELETE')")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Xóa sản phẩm thành công");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Không tìm thấy sản phẩm");
        }
    }

    @GetMapping("/site-setting/list")
    public ResponseEntity<Map<String, Object>> getListSiteSetting(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<SiteSetting> siteSettingPage = siteSettingService.getAllSiteSettings(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", siteSettingPage.getContent());
        response.put("pageNumber", siteSettingPage.getNumber() + 1);
        response.put("pageSize", siteSettingPage.getSize());
        response.put("totalPages", siteSettingPage.getTotalPages());
        response.put("totalElements", siteSettingPage.getTotalElements());
        response.put("last", siteSettingPage.isLast());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/site-setting/edit/{key}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/site-setting/**', 'PUT')")
    public SiteSetting updateSiteSetting(@PathVariable int key, @RequestBody SiteSetting siteSetting) {
        return siteSettingService.updateSiteSetting(key, siteSetting);
    }

    @GetMapping("/article/list")
    public ResponseEntity<Map<String, Object>> getListArticle(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Article> articlePage = articleService.getAllArticles(pageable);

        List<Article> articles = articlePage.getContent();
        for (Article article : articles) {
            if (article.getFeaturedImageUrl() != null) {
                article.setFeaturedImageUrl("/images/" + article.getFeaturedImageUrl());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", articles);
        response.put("pageNumber", articlePage.getNumber() + 1);
        response.put("pageSize", articlePage.getSize());
        response.put("totalPages", articlePage.getTotalPages());
        response.put("totalElements", articlePage.getTotalElements());
        response.put("last", articlePage.isLast());

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/article/create", consumes = {"multipart/form-data"})
     @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/article/**', 'POST')")
    public ResponseEntity<Article> createArticle(
            @RequestPart("article") String articleJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            ArticleRequest dto = objectMapper.readValue(articleJson, ArticleRequest.class);
            Article article = articleService.createArticleFromDTO(dto, imageFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(article);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping(value = "/article/edit/{id}", consumes = {"multipart/form-data"})
     @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/article/**', 'PUT')")
    public ResponseEntity<Article> updateArticle(
            @PathVariable int id,
            @RequestPart("article") String articleJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            ArticleRequest dto = objectMapper.readValue(articleJson, ArticleRequest.class);
            Article updatedArticle = articleService.updateArticle(id, dto, imageFile);
            return ResponseEntity.ok(updatedArticle);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/article/delete/{id}")
     @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/article/**', 'DELETE')") 
    public ResponseEntity<String> delete(@PathVariable int id) {
        try {
            articleService.deleteArticle(id);
            return new ResponseEntity<>("Xóa thành công", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Không tìm thấy nội dung cần xóa");
        }
    }

    @GetMapping("/material/list")
    public ResponseEntity<Map<String, Object>> getAllMaterials(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Material> materialPage = materialService.getAllMaterials(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", materialPage.getContent());
        response.put("pageNumber", materialPage.getNumber() + 1);
        response.put("pageSize", materialPage.getSize());
        response.put("totalPages", materialPage.getTotalPages());
        response.put("totalElements", materialPage.getTotalElements());
        response.put("last", materialPage.isLast());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/material/create")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/material/**', 'POST')")
    public ResponseEntity<?> createMaterial(@RequestBody Material material) {
        try {
            Material saved = materialService.createMaterial(material);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/material/edit/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/material/**', 'PUT')")
    public ResponseEntity<?> updateMaterial(@PathVariable int id, @RequestBody Material updatedMaterial) {
        try {
            Material updated = materialService.updateMaterial(id, updatedMaterial);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy vật liệu");
        }
    }

    @DeleteMapping("/material/delete/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/material/**', 'DELETE')")
    public ResponseEntity<?> deleteMaterial(@PathVariable int id) {
        try {
            materialService.deleteMaterial(id);
            return ResponseEntity.ok("Xóa vật liệu thành công");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy vật liệu");
        }
    }

    @GetMapping("/material/search")
    public ResponseEntity<List<Material>> searchMaterials(@RequestParam String name) {
        return ResponseEntity.ok(materialService.searchMaterialsByName(name));
    }

    @GetMapping("/inquiry/list")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/inquiry/**', 'GET')")
    public ResponseEntity<Map<String, Object>> getAllInquiries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Inquiry> inquiryPage = inquiryService.getAllInquiries(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", inquiryPage.getContent());
        response.put("pageNumber", inquiryPage.getNumber() + 1);
        response.put("pageSize", inquiryPage.getSize());
        response.put("totalPages", inquiryPage.getTotalPages());
        response.put("totalElements", inquiryPage.getTotalElements());
        response.put("last", inquiryPage.isLast());
        return ResponseEntity.ok(response);
    }
}

record FeedbackRequest(String feedback) {}