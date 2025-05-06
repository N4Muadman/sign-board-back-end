package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.request.ProductRequest;
import com.techbytedev.signboardmanager.dto.request.UserUpdateRequest;
import com.techbytedev.signboardmanager.dto.response.ProductResponse;
import com.techbytedev.signboardmanager.dto.response.UserResponse;
import com.techbytedev.signboardmanager.entity.Category;
import com.techbytedev.signboardmanager.entity.Contact;
import com.techbytedev.signboardmanager.entity.SiteSetting;
import com.techbytedev.signboardmanager.entity.UserDesign;
import com.techbytedev.signboardmanager.repository.UserDesignRepository;
import com.techbytedev.signboardmanager.service.CategoryService;
import com.techbytedev.signboardmanager.service.ContactService;
import com.techbytedev.signboardmanager.service.ProductService;
import com.techbytedev.signboardmanager.service.SiteSettingService;
import com.techbytedev.signboardmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserDesignRepository userDesignRepository;
    private final UserService userService;
    private final ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ContactService contactService;

    private final SiteSettingService siteSettingService;

    public AdminController(UserDesignRepository userDesignRepository, UserService userService, ProductService productService, SiteSettingService siteSettingService) {
        this.userDesignRepository = userDesignRepository;
        this.userService = userService;
        this.productService = productService;
        this.siteSettingService = siteSettingService;
    }

    // --- API quản lý thiết kế (UserDesign) ---

    @GetMapping("/designs")
    public List<UserDesign> getSubmittedDesigns() {
        return userDesignRepository.findAll().stream()
                .filter(design -> design.getStatus() == UserDesign.Status.SUBMITTED)
                .toList();
    }

    @GetMapping("/designs/all")
    public List<UserDesign> getAllDesigns() {
        return userDesignRepository.findAll();
    }

    @GetMapping("/designs/{id}")
    public ResponseEntity<UserDesign> getDesignById(@PathVariable Integer id) {
        UserDesign design = userDesignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Design not found with id: " + id));
        return ResponseEntity.ok(design);
    }

    @PutMapping("/designs/{id}/status")
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
    public ResponseEntity<UserDesign> sendFeedback(
            @PathVariable Integer id,
            @RequestBody FeedbackRequest request) {
        UserDesign design = userDesignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Design not found with id: " + id));

        design.setUserFeedback(request.feedback());
        design.setUpdatedAt(LocalDateTime.now());
        userDesignRepository.save(design);
        return ResponseEntity.ok(design);
    }

    // --- API quản lý người dùng (User) ---

    @GetMapping("/users")
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/users/{id}")
    public UserResponse getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    // API lọc và tìm kiếm người dùng với phân trang
    @GetMapping("/users/search")
    public Page<UserResponse> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        return userService.searchUsers(username, email, roleName, isActive, pageable);
    }

    @PutMapping("/users/{id}")
    public UserResponse updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/users/{id}/assign-admin")
    public ResponseEntity<UserResponse> assignAdminRole(@PathVariable Integer id) {
        UserResponse updatedUser = userService.assignAdminRole(id);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/users/{id}/remove-admin")
    public ResponseEntity<UserResponse> removeAdminRole(@PathVariable Integer id) {
        UserResponse updatedUser = userService.removeAdminRole(id);
        return ResponseEntity.ok(updatedUser);
    }

    // --- API quản lý danh mục (Category) ---

    @PostMapping("/category/create")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category saveCategory = categoryService.saveCategory(category);
        return new ResponseEntity<>(saveCategory, HttpStatus.CREATED);
    }

    @PutMapping("/category/edit/{id}")
    public Category updateCategory(@PathVariable int id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/category/delete/{id}")
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

    // --- API quản lý liên hệ (Contact) ---

    @PostMapping("/contact/create")
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        Contact saveContact = contactService.saveContact(contact);
        return new ResponseEntity<>(saveContact, HttpStatus.CREATED);
    }

    // --- API quản lý sản phẩm (Product) ---

    @PostMapping("/product/create")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.createProduct(productRequest);
        return ResponseEntity.ok(productResponse);
    }

    @PutMapping("/product/edit/{id}")
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
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Xóa sản phẩm thành công");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Không tìm thấy sản phẩm");
        }
    }

    // --- API quản lý cài đặt site (SiteSetting) ---

    @PutMapping("/site-setting/edit/{key}")
    public SiteSetting updateSiteSetting(@PathVariable int key, @RequestBody SiteSetting siteSetting) {
        return siteSettingService.updateSiteSetting(key, siteSetting);
    }
}

record UpdateDesignStatusRequest(UserDesign.Status status, String notes) {}
record FeedbackRequest(String feedback) {}