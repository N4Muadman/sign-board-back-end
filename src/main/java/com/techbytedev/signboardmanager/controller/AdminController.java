package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.request.ArticleRequest;
import com.techbytedev.signboardmanager.dto.request.ProductRequest;
import com.techbytedev.signboardmanager.dto.request.UserCreateRequest;
import com.techbytedev.signboardmanager.dto.request.UserUpdateRequest;
import com.techbytedev.signboardmanager.dto.response.CustomPageResponse;
import com.techbytedev.signboardmanager.dto.response.ProductResponse;
import com.techbytedev.signboardmanager.dto.response.UserResponse;
import com.techbytedev.signboardmanager.entity.*;
import com.techbytedev.signboardmanager.repository.UserDesignRepository;
import com.techbytedev.signboardmanager.service.*;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserDesignRepository userDesignRepository;
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ContactService contactService;
    private final ArticleService articleService;
    private final SiteSettingService siteSettingService;
    private final MaterialService materialService;

    public AdminController(UserDesignRepository userDesignRepository, UserService userService, ProductService productService, CategoryService categoryService, ContactService contactService, ArticleService articleService, SiteSettingService siteSettingService, MaterialService materialService) {
        this.userDesignRepository = userDesignRepository;
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.contactService = contactService;
        this.articleService = articleService;
        this.siteSettingService = siteSettingService;
        this.materialService = materialService;
    }

    // Quản lý thiết kế
    @GetMapping("/designs")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/designs/**', 'GET')")
    public List<UserDesign> getSubmittedDesigns() {
        return userDesignRepository.findAll().stream()
                .filter(design -> design.getStatus() == UserDesign.Status.SUBMITTED)
                .toList();
    }

    @GetMapping("/designs/all")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/designs/**', 'GET')")
    public List<UserDesign> getAllDesigns() {
        return userDesignRepository.findAll();
    }

    @GetMapping("/designs/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/designs/**', 'GET')")
    public ResponseEntity<UserDesign> getDesignById(@PathVariable Integer id) {
        UserDesign design = userDesignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Design not found with id: " + id));
        return ResponseEntity.ok(design);
    }

    @PutMapping("/designs/{id}/status")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/designs/**', 'PUT')")
    public ResponseEntity<UserDesign> updateDesignStatus(
            @PathVariable Integer id,
            @RequestBody UpdateDesignStatusRequest request) {
        UserDesign design = userDesignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Design not found with id: " + id));

        design.setStatus(request.status());
        if (request.notes() != null) {
            design.setNotes(request.notes());
        }
        design.setUpdatedAt(LocalDateTime.now());
        userDesignRepository.save(design);

        return ResponseEntity.ok(design);
    }

    @PutMapping("/designs/{id}/feedback")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/designs/**', 'PUT')")
    public ResponseEntity<UserDesign> sendFeedback(
            @PathVariable Integer id,
            @RequestBody FeedbackRequest request) {
        UserDesign design = userDesignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Design not found with id: " + id));

        design.setUpdatedAt(LocalDateTime.now());
        userDesignRepository.save(design);
        return ResponseEntity.ok(design);
    }

    // Quản lý người dùng
    @GetMapping("/users")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'GET')")
    public CustomPageResponse<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        int adjustedPage = page - 1;
        if (adjustedPage < 0) {
            adjustedPage = 0;
        }
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
        if (adjustedPage < 0) {
            adjustedPage = 0;
        }
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
    @GetMapping("/category/list")
    public ResponseEntity<Map<String, Object>> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Category> categoryPage = categoryService.getAllCategories(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", categoryPage.getContent());
        response.put("pageNumber", categoryPage.getNumber() + 1);
        response.put("pageSize", categoryPage.getSize());
        response.put("totalPages", categoryPage.getTotalPages());
        response.put("totalElements", categoryPage.getTotalElements());
        response.put("last", categoryPage.isLast());

        return ResponseEntity.ok(response);
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
    // danh sách liên hệ
    @GetMapping("/contact/list")
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
    // lấy danh sách sản phẩm
    @GetMapping("/product/list")
    public ResponseEntity<Map<String, Object>> getList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> productPage = productService.findAll(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", productPage.getContent());
        response.put("pageNumber", productPage.getNumber() + 1);
        response.put("pageSize", productPage.getSize());
        response.put("totalPages", productPage.getTotalPages());
        response.put("totalElements", productPage.getTotalElements());
        response.put("last", productPage.isLast());

        return ResponseEntity.ok(response);
    }
    @PostMapping("/product/create")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/product/create', 'POST')")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.createProduct(productRequest);
        return ResponseEntity.ok(productResponse);
    }

    @PutMapping("/product/edit/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/product/edit/{id}', 'PUT')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("id") int productId,
            @RequestBody ProductRequest productRequest) {
        try {
            ProductResponse response = productService.updateProduct(productId, productRequest);
            return ResponseEntity.ok(response);
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
    // hiển thị
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
    // hiển thị danh sách article
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

    // thêm
    @PostMapping("/article/create")
    public ResponseEntity<Article> createArticle(@RequestBody ArticleRequest dto) {
        try {
            Article article = articleService.createArticleFromDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(article);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    // up ảnh riêng
    @PostMapping("/article/{id}/upload-image")
    public ResponseEntity<String> uploadImage(@PathVariable int id, @RequestParam("file") MultipartFile file) {
        try {
            articleService.uploadImage(id, file);
            return ResponseEntity.ok("Upload successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Upload failed");
        }
    }
    // sửa
    @PutMapping("/article/edit/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable int id, @RequestBody ArticleRequest dto) {
        try {
            Article updatedArticle = articleService.updateArticle(id, dto);
            return ResponseEntity.ok(updatedArticle);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    // xóa
    @DeleteMapping("/article/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        try {
            articleService.deleteArticle(id);
            return new ResponseEntity<>("Xóa thành công", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Không tìm thấy nội dung cần xóa");
        }
    }
    // hiển thị danh sách
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
    // thêm
    @PostMapping("/material/create")
    public ResponseEntity<?> createMaterial(@RequestBody Material material) {
        try {
            Material saved = materialService.createMaterial(material);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
    // sửa
    @PutMapping("/material/edit/{id}")
    public ResponseEntity<?> updateMaterial(@PathVariable int id, @RequestBody Material updatedMaterial) {
        try {
            Material updated = materialService.updateMaterial(id, updatedMaterial);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy vật liệu");
        }
    }
    // xóa
    @DeleteMapping("/material/delete/{id}")
    public ResponseEntity<?> deleteMaterial(@PathVariable int id) {
        try {
            materialService.deleteMaterial(id);
            return ResponseEntity.ok("Xóa vật liệu thành công");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy vật liệu");
        }
    }

    // tìm kiếm theo tên
    @GetMapping("/material/search")
    public ResponseEntity<List<Material>> searchMaterials(@RequestParam String name) {
        return ResponseEntity.ok(materialService.searchMaterialsByName(name));
    }
}

record UpdateDesignStatusRequest(UserDesign.Status status, String notes) {}
record FeedbackRequest(String feedback) {}