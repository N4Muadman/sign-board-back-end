package com.techbytedev.signboardmanager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Insertion Utility for SignBoard Manager
 * This class executes all the necessary INSERT statements to populate the database with initial data
 *
 * Run this class to insert:
 * - 70 permissions
 * - 2 roles (ADMIN, USER)
 * - 2 users (admin, test user)
 * - 19 categories (9 main + 10 subcategories)
 * - 16 materials
 * - 45 products
 * - 4 contacts
 * - Role-permission mappings
 * - Product images
 * - Product-material relationships
 * - 4 inquiries
 * - 3 design templates
 * - Site settings
 * - 3 banners
 * - 2 articles
 *
 * Default password for all users: 'password'
 */
@Component
public class DataInsertionUtility implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInsertionUtility.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data insertion for SignBoard Manager...");

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement()) {
                 var rs = statement.executeQuery("SELECT COUNT(*) FROM roles");
    rs.next();
    if (rs.getInt(1) > 0) {
        logger.info("Database already seeded, skipping data insertion.");
        return;
    }

                // // =============================================
                // // STEP 0: CLEAN UP TABLES
                // // =============================================
                // logger.info("Disabling foreign key checks and cleaning tables...");
                // statement.execute("SET FOREIGN_KEY_CHECKS = 0;");

                // statement.execute("TRUNCATE TABLE cms_articles;");
                // statement.execute("TRUNCATE TABLE banners;");
                // statement.execute("TRUNCATE TABLE site_settings;");
                // statement.execute("TRUNCATE TABLE design_templates;");
                // statement.execute("TRUNCATE TABLE inquiries;");
                // statement.execute("TRUNCATE TABLE product_materials;");
                // statement.execute("TRUNCATE TABLE product_images;");
                // statement.execute("TRUNCATE TABLE role_permissions;");
                // statement.execute("TRUNCATE TABLE users;");
                // statement.execute("TRUNCATE TABLE products;");
                // statement.execute("TRUNCATE TABLE materials;");
                // statement.execute("TRUNCATE TABLE product_categories;");
                // statement.execute("TRUNCATE TABLE roles;");
                // statement.execute("TRUNCATE TABLE permissions;");
                // statement.execute("TRUNCATE TABLE contact_submissions;");

                // statement.execute("SET FOREIGN_KEY_CHECKS = 1;");
                // logger.info("Tables cleaned successfully.");


                // =============================================
                // STEP 1: PERMISSIONS FIRST (Required for roles)
                // =============================================
                logger.info("Inserting permissions...");

                // Role Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ROLE_CREATE', '/api/admin/roles', 'POST', 'ROLE', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ROLE_READ', '/api/admin/roles/{id}', 'GET', 'ROLE', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ROLE_UPDATE', '/api/admin/roles/{id}', 'PUT', 'ROLE', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ROLE_DELETE', '/api/admin/roles/{id}', 'DELETE', 'ROLE', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ROLE_LIST', '/api/admin/roles', 'GET', 'ROLE', NOW(), NOW())");

                // User Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('USER_CREATE', '/api/admin/users/create', 'POST', 'USER', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('USER_READ', '/api/admin/users/{id}', 'GET', 'USER', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('USER_UPDATE', '/api/admin/users/{id}', 'PUT', 'USER', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('USER_DELETE', '/api/admin/users/{id}', 'DELETE', 'USER', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('USER_LIST', '/api/admin/users', 'GET', 'USER', NOW(), NOW())");

                // Category Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('CATEGORY_CREATE', '/api/admin/category/create', 'POST', 'CATEGORY', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('CATEGORY_READ', '/api/admin/category/{id}', 'GET', 'CATEGORY', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('CATEGORY_UPDATE', '/api/admin/category/edit/{id}', 'PUT', 'CATEGORY', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('CATEGORY_DELETE', '/api/admin/category/delete/{id}', 'DELETE', 'CATEGORY', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('CATEGORY_LIST', '/api/admin/category', 'GET', 'CATEGORY', NOW(), NOW())");

                // Product Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('PRODUCT_CREATE', '/api/admin/product/create', 'POST', 'PRODUCT', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('PRODUCT_READ', '/api/admin/product/{id}', 'GET', 'PRODUCT', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('PRODUCT_UPDATE', '/api/admin/product/edit/{id}', 'PUT', 'PRODUCT', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('PRODUCT_DELETE', '/api/admin/product/delete/{id}', 'DELETE', 'PRODUCT', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('PRODUCT_LIST', '/api/admin/product', 'GET', 'PRODUCT', NOW(), NOW())");

                // Design Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('DESIGN_CREATE', '/api/admin/user-designs', 'POST', 'DESIGN', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('DESIGN_READ', '/api/admin/user-designs/{id}', 'GET', 'DESIGN', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('DESIGN_UPDATE', '/api/admin/user-designs/{id}', 'PUT', 'DESIGN', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('DESIGN_DELETE', '/api/admin/user-designs/{id}', 'DELETE', 'DESIGN', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('DESIGN_LIST', '/api/admin/user-designs', 'GET', 'DESIGN', NOW(), NOW())");

                // Article Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ARTICLE_CREATE', '/api/admin/article/create', 'POST', 'ARTICLE', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ARTICLE_READ', '/api/admin/article/{id}', 'GET', 'ARTICLE', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ARTICLE_UPDATE', '/api/admin/article/edit/{id}', 'PUT', 'ARTICLE', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ARTICLE_DELETE', '/api/admin/article/delete/{id}', 'DELETE', 'ARTICLE', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ARTICLE_LIST', '/api/admin/article', 'GET', 'ARTICLE', NOW(), NOW())");

                // Article Category Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ARTICLE_CATEGORY_CREATE', '/api/admin/article-categories', 'POST', 'ARTICLE_CATEGORY', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ARTICLE_CATEGORY_READ', '/api/admin/article-categories/{id}', 'GET', 'ARTICLE_CATEGORY', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ARTICLE_CATEGORY_UPDATE', '/api/admin/article-categories/{id}', 'PUT', 'ARTICLE_CATEGORY', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ARTICLE_CATEGORY_DELETE', '/api/admin/article-categories/{id}', 'DELETE', 'ARTICLE_CATEGORY', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ARTICLE_CATEGORY_LIST', '/api/admin/article-categories', 'GET', 'ARTICLE_CATEGORY', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ARTICLE_CATEGORY_TREE', '/api/admin/article-categories/tree', 'GET', 'ARTICLE_CATEGORY', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('ARTICLE_CATEGORY_SEARCH', '/api/admin/article-categories/search', 'GET', 'ARTICLE_CATEGORY', NOW(), NOW())");

                // Banner Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('BANNER_CREATE', '/api/admin/banners', 'POST', 'BANNER', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('BANNER_READ', '/api/admin/banners/{id}', 'GET', 'BANNER', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('BANNER_UPDATE', '/api/admin/banners/{id}', 'PUT', 'BANNER', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('BANNER_DELETE', '/api/admin/banners/{id}', 'DELETE', 'BANNER', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('BANNER_LIST', '/api/admin/banners', 'GET', 'BANNER', NOW(), NOW())");

                // Inquiry Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('INQUIRY_READ', '/api/admin/inquiry/list/{id}', 'GET', 'INQUIRY', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('INQUIRY_UPDATE', '/api/admin/inquiry', 'PUT', 'INQUIRY', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('INQUIRY_LIST', '/api/admin/inquiry/list', 'GET', 'INQUIRY', NOW(), NOW())");

                // Contact Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('CONTACT_CREATE', '/api/admin/contacts', 'POST', 'CONTACT', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('CONTACT_READ', '/api/admin/contacts/{id}', 'GET', 'CONTACT', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('CONTACT_UPDATE', '/api/admin/contacts/{id}', 'PUT', 'CONTACT', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('CONTACT_DELETE', '/api/admin/contacts/{id}', 'DELETE', 'CONTACT', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('CONTACT_LIST', '/api/admin/contacts', 'GET', 'CONTACT', NOW(), NOW())");

                // Design Template Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('DESIGN_TEMPLATE_CREATE', '/api/admin/design-templates', 'POST', 'DESIGN_TEMPLATE', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('DESIGN_TEMPLATE_READ', '/api/admin/design-templates/{id}', 'GET', 'DESIGN_TEMPLATE', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('DESIGN_TEMPLATE_UPDATE', '/api/design-templates/{id}', 'PUT', 'DESIGN_TEMPLATE', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('DESIGN_TEMPLATE_DELETE', '/api/design-templates/{id}', 'DELETE', 'DESIGN_TEMPLATE', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('DESIGN_TEMPLATE_LIST', '/api/design-templates', 'GET', 'DESIGN_TEMPLATE', NOW(), NOW())");

                // Material Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('MATERIAL_CREATE', '/api/materials', 'POST', 'MATERIAL', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('MATERIAL_READ', '/api/materials/{id}', 'GET', 'MATERIAL', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('MATERIAL_UPDATE', '/api/materials/{id}', 'PUT', 'MATERIAL', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('MATERIAL_DELETE', '/api/materials/{id}', 'DELETE', 'MATERIAL', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('MATERIAL_LIST', '/api/materials', 'GET', 'MATERIAL', NOW(), NOW())");

                // Wishlist Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('WISHLIST_CREATE', '/api/wishlists', 'POST', 'WISHLIST', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('WISHLIST_READ', '/api/wishlists/{id}', 'GET', 'WISHLIST', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('WISHLIST_UPDATE', '/api/wishlists/{id}', 'PUT', 'WISHLIST', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('WISHLIST_DELETE', '/api/wishlists/{id}', 'DELETE', 'WISHLIST', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('WISHLIST_LIST', '/api/wishlists', 'GET', 'WISHLIST', NOW(), NOW())");

                // Site Settings Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('SITE_SETTING_CREATE', '/api/site-settings', 'POST', 'SITE_SETTING', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('SITE_SETTING_READ', '/api/site-settings/{id}', 'GET', 'SITE_SETTING', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('SITE_SETTING_UPDATE', '/api/site-settings/{id}', 'PUT', 'SITE_SETTING', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('SITE_SETTING_DELETE', '/api/site-settings/{id}', 'DELETE', 'SITE_SETTING', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('SITE_SETTING_LIST', '/api/site-settings', 'GET', 'SITE_SETTING', NOW(), NOW())");

                // Permission Management
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('PERMISSION_CREATE', '/api/admin/permissions', 'POST', 'PERMISSION', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('PERMISSION_READ', '/api/admin/permissions/{id}', 'GET', 'PERMISSION', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('PERMISSION_UPDATE', '/api/admin/permissions', 'PUT', 'PERMISSION', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('PERMISSION_DELETE', '/api/admin/permissions/{id}', 'DELETE', 'PERMISSION', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO permissions (name, api_path, method, module, created_at, updated_at) VALUES " +
                    "('PERMISSION_LIST', '/api/admin/permissions', 'GET', 'PERMISSION', NOW(), NOW())");

                // =============================================
                // STEP 2: ROLES (After permissions)
                // =============================================
                logger.info("Inserting roles...");

                executeInsert(statement, "INSERT INTO roles (name, description, active, created_at, updated_at) VALUES " +
                        "('ADMIN', 'Administrator with most system access', true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO roles (name, description, active, created_at, updated_at) VALUES " +
                        "('USER', 'Regular user with basic access', true, NOW(), NOW())");

                // =============================================
                // STEP 2.5: ROLE-PERMISSIONS (After roles, before users)
                // =============================================
                logger.info("Inserting role-permission relationships...");

                // ADMIN Role - Full access to all permissions
                executeInsert(statement, "INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at) " +
                        "SELECT r.id, p.id, NOW(), NOW() FROM roles r, permissions p WHERE r.name = 'ADMIN'");

                // USER Role - Basic user access
                executeInsert(statement, "INSERT INTO role_permissions (role_id, permission_id, created_at, updated_at) " +
                        "SELECT r.id, p.id, NOW(), NOW() FROM roles r, permissions p WHERE r.name = 'USER' " +
                        "AND p.module IN ('PRODUCT', 'WISHLIST', 'CONTACT')");

                // =============================================
                // STEP 3: USERS (After roles and role-permissions)
                // =============================================

                executeInsert(statement, "INSERT INTO users (role_id, role_name, username, email, password_hash, full_name, phone_number, address, is_active, email_verified_at, remember_token, created_at, updated_at) VALUES " +
    "(1, 'ADMIN', 'admin_signboard', 'admin@signboard.com', '$2a$10$MfBD/gIsV/lzJerUdJQrq.p9Cw6bOWKGLHdFE/qCze4vzxJvaLIPe', 'Administrator SignBoard', '+84901234567', '123 Admin Street, Ho Chi Minh City', true, NOW(), NULL, NOW(), NOW())");
executeInsert(statement, "INSERT INTO users (role_id, role_name, username, email, password_hash, full_name, phone_number, address, is_active, email_verified_at, remember_token, created_at, updated_at) VALUES " +
    "(2, 'USER', 'user_test', 'user.test@signboard.com', '$2a$10$MfBD/gIsV/lzJerUdJQrq.p9Cw6bOWKGLHdFE/qCze4vzxJvaLIPe', 'Test User', '+1234567890', '123 Test Ave, Test City', true, NOW(), NULL, NOW(), NOW())");

                // =============================================
                // STEP 4: CATEGORIES
                // =============================================
                logger.info("Inserting categories...");

                // Main Categories - Level 1
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Biển LED', 'bien-led', 'Các loại biển quảng cáo sử dụng đèn LED', 'bien-led.jpg', NULL, 1, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Biển Neon', 'bien-neon', 'Biển quảng cáo đèn neon truyền thống', 'bien-neon.jpg', NULL, 2, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Biển Alu', 'bien-alu', 'Biển quảng cáo làm từ nhôm alu', 'bien-alu.jpg', NULL, 3, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Biển Mica', 'bien-mica', 'Biển quảng cáo làm từ chất liệu mica', 'bien-mica.jpg', NULL, 4, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Biển Inox', 'bien-inox', 'Biển quảng cáo làm từ inox không gỉ', 'bien-inox.jpg', NULL, 5, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Biển Hiflex', 'bien-hiflex', 'Biển quảng cáo bạt hiflex', 'bien-hiflex.jpg', NULL, 6, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Chữ Nổi', 'chu-noi', 'Chữ nổi 3D các loại', 'chu-noi.jpg', NULL, 7, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Hộp Đèn', 'hop-den', 'Hộp đèn quảng cáo các loại', 'hop-den.jpg', NULL, 8, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Biển Quảng Cáo', 'bien-quang-cao', 'Biển quảng cáo tổng hợp', 'bien-quang-cao.jpg', NULL, 9, NOW(), NOW())");

                // Subcategories - Level 2
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('LED Thường', 'led-thuong', 'Biển LED thường', 'led-thuong.jpg', 1, 1, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('LED Ma Trận', 'led-ma-tran', 'Biển LED ma trận', 'led-ma-tran.jpg', 1, 2, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('LED Chạy Chữ', 'led-chay-chu', 'Biển LED chạy chữ', 'led-chay-chu.jpg', 1, 3, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Chữ Nổi Mica', 'chu-noi-mica', 'Chữ nổi làm từ mica', 'chu-noi-mica.jpg', 7, 1, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Chữ Nổi Inox', 'chu-noi-inox', 'Chữ nổi làm từ inox', 'chu-noi-inox.jpg', 7, 2, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Chữ Nổi LED', 'chu-noi-led', 'Chữ nổi có đèn LED', 'chu-noi-led.jpg', 7, 3, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Hộp Đèn Mica', 'hop-den-mica', 'Hộp đèn làm từ mica', 'hop-den-mica.jpg', 8, 1, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Hộp Đèn Siêu Mỏng', 'hop-den-sieu-mong', 'Hộp đèn siêu mỏng', 'hop-den-sieu-mong.jpg', 8, 2, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Bạt Hiflex Xuyên Sáng', 'bat-hiflex-xuyen-sang', 'Bạt hiflex xuyên sáng', 'bat-hiflex-xuyen-sang.jpg', 6, 1, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO product_categories (name, slug, description, image_url, parent_category_id, sort_order, created_at, updated_at) VALUES " +
                        "('Bạt Hiflex Không Xuyên Sáng', 'bat-hiflex-khong-xuyen-sang', 'Bạt hiflex không xuyên sáng', 'bat-hiflex-khong-xuyen-sang.jpg', 6, 2, NOW(), NOW())");

                // STEP 5: MATERIALS
                // =============================================
                logger.info("Inserting materials...");

                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Tấm Alu Ngoài Trời', 'Tấm nhôm alu chuyên dụng cho biển ngoài trời', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Tấm Alu Trong Nhà', 'Tấm nhôm alu chuyên dụng cho biển trong nhà', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Mặt Dựng Alu', 'Mặt dựng nhôm alu cao cấp', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Đèn LED Trắng', 'Đèn LED trắng công suất cao', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Đèn LED RGB', 'Đèn LED đổi màu RGB', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Tấm Mica Trong Suốt', 'Tấm mica trong suốt dùng cho biển quảng cáo', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Tấm Mica Màu', 'Tấm mica màu sắc đa dạng', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Inox Vàng Gương', 'Tấm inox vàng gương bóng cao cấp', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Inox Trắng', 'Tấm inox trắng không gỉ', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Bạt Hiflex Xuyên Sáng', 'Bạt hiflex xuyên sáng dùng cho hộp đèn', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Bạt Hiflex Không Xuyên Sáng', 'Bạt hiflex không xuyên sáng dùng cho biển bạt', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Đèn Neon Đỏ', 'Đèn neon màu đỏ truyền thống', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Đèn Neon Xanh Dương', 'Đèn neon màu xanh dương', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Khung Sắt Hộp', 'Khung sắt hộp dùng cho biển quảng cáo', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Decal Trong Suốt', 'Decal trong suốt dùng cho biển', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO materials (name, description, created_at, updated_at) VALUES " +
                        "('Decal Màu', 'Decal màu sắc đa dạng', NOW(), NOW())");

                // =============================================
                // STEP 6: PRODUCTS (After categories)
                // =============================================
                logger.info("Inserting products...");

                // Biển LED Products
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(1, 'Biển LED Quảng Cáo Shop Quần Áo', 'bien-led-shop-quan-ao', 'Biển LED cho shop quần áo với thiết kế hiện đại', '200cm x 80cm x 10cm', 3500000.00, 5.00, 3325000.00, 'LED-SHOP-001', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(1, 'Biển LED Pet Shop', 'bien-led-pet-shop', 'Biển LED chuyên dụng cho cửa hàng thú cưng', '150cm x 60cm x 8cm', 2800000.00, 0.00, 2800000.00, 'LED-PET-002', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(1, 'Biển LED Ma Trận Nhà Hàng', 'bien-led-ma-tran-nha-hang', 'Biển LED ma trận hiển thị thông tin nhà hàng', '300cm x 100cm x 12cm', 8500000.00, 10.00, 7650000.00, 'LED-MATRIX-003', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(1, 'Biển LED Chạy Chữ Khai Trương', 'bien-led-chay-chu-khai-truong', 'Biển LED chạy chữ cho khai trương cửa hàng', '180cm x 50cm x 8cm', 4200000.00, 8.00, 3864000.00, 'LED-RUNNING-004', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(1, 'Biển LED Hiệu Thuốc', 'bien-led-hieu-thuoc', 'Biển LED cho hiệu thuốc với chữ thập xanh', '120cm x 80cm x 10cm', 3200000.00, 0.00, 3200000.00, 'LED-PHARMACY-005', false, true, NOW(), NOW())");

                // Biển Neon Products
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(2, 'Biển Neon Quán Cafe', 'bien-neon-quan-cafe', 'Biển neon trang trí quán cafe vintage', '100cm x 40cm x 8cm', 2500000.00, 5.00, 2375000.00, 'NEON-CAFE-001', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(2, 'Biển Neon Barber Shop', 'bien-neon-barber-shop', 'Biển neon cho tiệm cắt tóc nam', '80cm x 30cm x 6cm', 1800000.00, 0.00, 1800000.00, 'NEON-BARBER-002', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(2, 'Biển Neon Hạt Sáng', 'bien-neon-hat-sang', 'Biển neon hạt sáng lung linh', '150cm x 60cm x 10cm', 4200000.00, 12.00, 3696000.00, 'NEON-SPARKLE-003', true, true, NOW(), NOW())");

                // Biển Alu Products
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(3, 'Biển Alu Ngoài Trời Spa', 'bien-alu-ngoai-troi-spa', 'Biển alu ngoài trời cho spa làm đẹp', '200cm x 100cm x 5cm', 1800000.00, 0.00, 1800000.00, 'ALU-SPA-001', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(3, 'Biển Alu Trong Nhà Văn Phòng', 'bien-alu-trong-nha-van-phong', 'Biển alu trong nhà cho văn phòng công ty', '80cm x 40cm x 3cm', 850000.00, 5.00, 807500.00, 'ALU-OFFICE-002', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(3, 'Biển Alu Công Trình', 'bien-alu-cong-trinh', 'Biển alu chỉ dẫn công trình xây dựng', '300cm x 150cm x 8cm', 5200000.00, 8.00, 4784000.00, 'ALU-CONSTRUCTION-003', true, true, NOW(), NOW())");

                // Biển Mica Products
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(4, 'Biển Mica Hút Nổi', 'bien-mica-hut-noi', 'Biển mica hút nổi với hiệu ứng 3D', '100cm x 60cm x 5cm', 1200000.00, 0.00, 1200000.00, 'MICA-3D-001', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(4, 'Biển Mica Phát Sáng', 'bien-mica-phat-sang', 'Biển mica phát sáng trong đêm', '150cm x 80cm x 8cm', 2800000.00, 10.00, 2520000.00, 'MICA-GLOW-002', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(4, 'Biển Mica Chữ Nổi', 'bien-mica-chu-noi', 'Biển mica kết hợp chữ nổi', '120cm x 50cm x 6cm', 1650000.00, 5.00, 1567500.00, 'MICA-LETTER-003', false, true, NOW(), NOW())");

                // Biển Inox Products
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(5, 'Biển Inox Ăn Mòn', 'bien-inox-an-mon', 'Biển inox ăn mòn hóa chất cao cấp', '80cm x 40cm x 3cm', 950000.00, 0.00, 950000.00, 'INOX-ETCHED-001', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(5, 'Biển Inox Vàng Gương', 'bien-inox-vang-guong', 'Biển inox vàng gương bóng sang trọng', '100cm x 50cm x 4cm', 1650000.00, 8.00, 1518000.00, 'INOX-GOLD-002', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(5, 'Biển Inox Chỉ Dẫn', 'bien-inox-chi-dan', 'Biển inox chỉ dẫn tòa nhà', '200cm x 60cm x 5cm', 2200000.00, 5.00, 2090000.00, 'INOX-DIRECTION-003', false, true, NOW(), NOW())");

                // Biển Hiflex Products
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(6, 'Biển Hiflex Xuyên Sáng', 'bien-hiflex-xuyen-sang', 'Biển hiflex xuyên sáng cho hộp đèn', '300cm x 200cm', 3200000.00, 10.00, 2880000.00, 'HIFLEX-BACKLIT-001', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(6, 'Biển Hiflex Không Xuyên Sáng', 'bien-hiflex-khong-xuyen-sang', 'Biển hiflex không xuyên sáng ngoài trời', '400cm x 250cm', 5800000.00, 12.00, 5104000.00, 'HIFLEX-OPAQUE-002', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(6, 'Biển Hiflex Khung Sắt', 'bien-hiflex-khung-sat', 'Biển hiflex khung sắt chắc chắn', '250cm x 150cm', 2850000.00, 0.00, 2850000.00, 'HIFLEX-FRAME-003', false, true, NOW(), NOW())");

                // Chữ Nổi Products
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(7, 'Chữ Nổi Mica Phát Sáng', 'chu-noi-mica-phat-sang', 'Chữ nổi mica phát sáng ban đêm', 'Cao 50cm', 850000.00, 5.00, 807500.00, 'LETTER-MICA-001', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(7, 'Chữ Nổi Inox Vàng Gương', 'chu-noi-inox-vang-guong', 'Chữ nổi inox vàng gương sang trọng', 'Cao 40cm', 1200000.00, 8.00, 1104000.00, 'LETTER-INOX-002', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(7, 'Chữ Nổi LED Viền', 'chu-noi-led-vien', 'Chữ nổi LED viền chạy quanh', 'Cao 60cm', 1800000.00, 10.00, 1620000.00, 'LETTER-LED-003', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(7, 'Chữ Nổi Mica Hút Nổi', 'chu-noi-mica-hut-noi', 'Chữ nổi mica hút nổi 3D', 'Cao 35cm', 650000.00, 0.00, 650000.00, 'LETTER-MICA-3D-004', false, true, NOW(), NOW())");

                // Hộp Đèn Products
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(8, 'Hộp Đèn Mica Siêu Mỏng', 'hop-den-mica-sieu-mong', 'Hộp đèn mica siêu mỏng tiết kiệm điện', '100cm x 60cm x 8cm', 950000.00, 5.00, 902500.00, 'LIGHTBOX-THIN-001', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(8, 'Hộp Đèn Hút Nổi', 'hop-den-hut-noi', 'Hộp đèn hút nổi với hiệu ứng nổi bật', '120cm x 80cm x 10cm', 1500000.00, 8.00, 1380000.00, 'LIGHTBOX-3D-002', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(8, 'Hộp Đèn Xuyên Sáng', 'hop-den-xuyen-sang', 'Hộp đèn xuyên sáng cho cửa hàng', '150cm x 100cm x 12cm', 2200000.00, 10.00, 1980000.00, 'LIGHTBOX-BACKLIT-003', false, true, NOW(), NOW())");

                // Biển Quảng Cáo Tổng Hợp (tiếp theo)
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(9, 'Biển Quảng Cáo Trung Tâm Thương Mại Cao Cấp', 'bien-quang-cao-trung-tam-thuong-mai-cao-cap', 'Biển quảng cáo trung tâm thương mại với thiết kế hiện đại và sang trọng', '600cm x 400cm', 35000000.00, 10.00, 31500000.00, 'ADS-CENTER-LUX-001', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(9, 'Biển Quảng Cáo Sự Kiện Triển Lãm', 'bien-quang-cao-su-kien-trien-lam', 'Biển quảng cáo chuyên dụng cho sự kiện và triển lãm thương mại', '400cm x 300cm', 15000000.00, 8.00, 13800000.00, 'ADS-EVENT-EXPO-002', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(9, 'Biển Quảng Cáo Đường Phố Cao Cấp', 'bien-quang-cao-duong-pho-cao-cap', 'Biển quảng cáo ngoài trời đường phố với chất lượng cao cấp', '500cm x 350cm', 22000000.00, 12.00, 19360000.00, 'ADS-STREET-PREMIUM-003', true, true, NOW(), NOW())");

                // Sản phẩm mới từ thư viện hình ảnh
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(1, 'Biển LED Quảng Cáo Shop Thời Trang Cao Cấp', 'bien-led-shop-thoi-trang-cao-cap', 'Biển LED shop thời trang với thiết kế hiện đại và ánh sáng nổi bật', '250cm x 100cm x 12cm', 8500000.00, 5.00, 8075000.00, 'LED-FASHION-LUX-031', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(1, 'Biển LED Pet Shop Hiện Đại', 'bien-led-pet-shop-hien-dai', 'Biển LED cửa hàng thú cưng với thiết kế thân thiện và màu sắc tươi sáng', '180cm x 80cm x 10cm', 5200000.00, 0.00, 5200000.00, 'LED-PET-MODERN-032', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(2, 'Biển Neon Quán Cafe Vintage', 'bien-neon-quan-cafe-vintage', 'Biển neon quán cafe với phong cách vintage cổ điển', '120cm x 50cm x 8cm', 4200000.00, 8.00, 3864000.00, 'NEON-CAFE-VINTAGE-033', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(3, 'Biển Alu Spa Sang Trọng', 'bien-alu-spa-sang-trong', 'Biển alu spa với thiết kế sang trọng và đẳng cấp', '200cm x 120cm x 6cm', 3800000.00, 0.00, 3800000.00, 'ALU-SPA-LUXURY-034', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(4, 'Biển Mica Hút Nổi 3D Cao Cấp', 'bien-mica-hut-noi-3d-cao-cap', 'Biển mica hút nổi với hiệu ứng 3D nổi bật', '150cm x 80cm x 8cm', 3200000.00, 5.00, 3040000.00, 'MICA-3D-PREMIUM-035', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(5, 'Biển Inox Vàng Gương Sang Trọng', 'bien-inox-vang-guong-sang-trong', 'Biển inox vàng gương với độ bóng cao và thiết kế tinh tế', '100cm x 60cm x 5cm', 2800000.00, 10.00, 2520000.00, 'INOX-GOLD-LUX-036', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(7, 'Chữ Nổi Mica Phát Sáng Ban Đêm', 'chu-noi-mica-phat-sang-ban-dem', 'Chữ nổi mica phát sáng lung linh trong đêm tối', 'Cao 60cm', 1200000.00, 0.00, 1200000.00, 'LETTER-MICA-GLOW-037', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(7, 'Chữ Nổi Inox Vàng Gương Cao Cấp', 'chu-noi-inox-vang-guong-cao-cap', 'Chữ nổi inox vàng gương với độ hoàn thiện cao cấp', 'Cao 50cm', 1800000.00, 8.00, 1656000.00, 'LETTER-INOX-GOLD-038', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(8, 'Hộp Đèn Mica Siêu Mỏng Hiện Đại', 'hop-den-mica-sieu-mong-hien-dai', 'Hộp đèn mica siêu mỏng với độ sáng cao và tiết kiệm điện', '120cm x 80cm x 6cm', 2200000.00, 5.00, 2090000.00, 'LIGHTBOX-THIN-MODERN-039', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(3, 'Biển Alu Văn Phòng Chuyên Nghiệp', 'bien-alu-van-phong-chuyen-nghiep', 'Biển alu văn phòng với thiết kế chuyên nghiệp và hiện đại', '90cm x 45cm x 4cm', 1500000.00, 0.00, 1500000.00, 'ALU-OFFICE-PRO-040', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(1, 'Biển LED Ma Trận Nhà Hàng Hiện Đại', 'bien-led-ma-tran-nha-hang-hien-dai', 'Biển LED ma trận nhà hàng với khả năng hiển thị đa dạng', '350cm x 120cm x 15cm', 12000000.00, 10.00, 10800000.00, 'LED-MATRIX-RESTAURANT-041', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(7, 'Chữ Nổi LED Viền Chạy Quanh', 'chu-noi-led-vien-chay-quanh', 'Chữ nổi LED với viền chạy quanh tạo hiệu ứng động', 'Cao 70cm', 2500000.00, 12.00, 2200000.00, 'LETTER-LED-RUNNING-042', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(8, 'Hộp Đèn Hút Nổi 3D', 'hop-den-hut-noi-3d', 'Hộp đèn hút nổi với hiệu ứng nổi bật và thu hút', '140cm x 90cm x 10cm', 3800000.00, 8.00, 3496000.00, 'LIGHTBOX-3D-043', true, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(5, 'Biển Inox Chỉ Dẫn Tòa Nhà', 'bien-inox-chi-dan-toa-nha', 'Biển inox chỉ dẫn tòa nhà với thiết kế chuyên nghiệp', '250cm x 80cm x 6cm', 4200000.00, 5.00, 3990000.00, 'INOX-DIRECTION-BUILDING-044', false, true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO products (category_id, name, slug, description, dimensions, price, discount_percent, discounted_price, sku, is_featured, is_active, created_at, updated_at) VALUES " +
                        "(6, 'Biển Hiflex Xuyên Sáng Cao Cấp', 'bien-hiflex-xuyen-sang-cao-cap', 'Biển hiflex xuyên sáng với chất lượng cao và độ bền tốt', '350cm x 250cm', 6500000.00, 10.00, 5850000.00, 'HIFLEX-BACKLIT-PREMIUM-045', true, true, NOW(), NOW())");

                // =============================================
                // STEP 7: CONTACTS
                // =============================================
                logger.info("Inserting contacts...");

                executeInsert(statement, "INSERT INTO contact_submissions (name, email, message, rating, created_at, updated_at) VALUES " +
                        "('John Customer', 'john.customer@example.com', 'Hi, I am interested in getting a quote for a custom LED sign for my business. Can you provide pricing information?', 5, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO contact_submissions (name, email, message, rating, created_at, updated_at) VALUES " +
                        "('Sarah Business Owner', 'sarah.owner@business.com', 'I need a custom neon sign for my restaurant. Looking for something that says \"Good Food Good Times\" in blue.', 5, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO contact_submissions (name, email, message, rating, created_at, updated_at) VALUES " +
                        "('Mike Retail Manager', 'mike.retail@store.com', 'We have an existing LED sign that needs repair. The lights are flickering. Can you help?', 5, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO contact_submissions (name, email, message, rating, created_at, updated_at) VALUES " +
                        "('Emily Restaurant Owner', 'emily.restaurant@food.com', 'Interested in your 32\" digital menu boards. How many units do you recommend for a medium-sized restaurant?', 5, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO contact_submissions (name, email, message, rating, created_at, updated_at) VALUES " +
                        "('David Store Owner', 'david.store@retail.com', 'I need a custom LED sign for my store. Can you provide a quote?', 5, NOW(), NOW())");
                // =============================================
                // STEP 9: PRODUCT IMAGES (After products)
                // =============================================
                // Hình ảnh cho các sản phẩm gốc (1-33)
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(1, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Trung Tâm Hiện Đại', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(1, 'bien-quang-cao-alu-chu-noi-dep-ha-noi-gia-re-scaled.jpg', 'Biển Alu Quảng Cáo Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(1, 'alu-ngoai-troi.jpg', 'Biển Alu Ngoài Trời Cao Cấp', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(2, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Sự Kiện Chuyên Nghiệp', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(2, 'bien-vay.jpg', 'Biển Vẫy Sự Kiện Hiện Đại', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(2, 'bien-vay-quang-cao-la-gi.jpg', 'Biển Vẫy Quảng Cáo Sự Kiện', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(3, 'bien-bat-hiflex.jpg', 'Biển Bạt Hiflex Đường Phố', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(3, 'biển-bạt-hiflex-khung-sắt.jpg', 'Biển Bạt Hiflex Khung Sắt', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(3, 'bạt-hiflex-xuyên-sáng.jpg', 'Biển Bạt Hiflex Xuyên Sáng', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(4, 'biển-quảng-cáo-den-neon.jpg', 'Biển Neon Quán Cafe Vintage', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(4, 'bien-den-led-huynh-quang.jpg', 'Biển Đèn Led Huỳnh Quang Cafe', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(4, 'biển-quảng-cáo-hat-sang.jpg', 'Biển Quảng Cáo Hắt Sáng Cafe', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(5, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Nhà Hàng Hiện Đại', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(5, 'bien-vay.jpg', 'Biển Vẫy Nhà Hàng Sang Trọng', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(5, 'bien-vay-trung-tam-hoi-nghi-tiec-cuoi-sen-vang-palace-1.jpg', 'Biển Vẫy Trung Tâm Hội Nghị', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(6, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Văn Phòng', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(6, 'alu-trong-nha.jpg', 'Biển Alu Trong Nhà Văn Phòng', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(6, 'tấm-nhôm-alu-trong-nhà.jpg', 'Biển Alu Trong Nhà Đẹp', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(7, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Cửa Hàng', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(7, 'bien-vay.jpg', 'Biển Vẫy Cửa Hàng Hiện Đại', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(7, 'bien-hop-den.jpg', 'Biển Hộp Đèn Cửa Hàng', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(8, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Siêu Thị', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(8, 'bien-vay.jpg', 'Biển Vẫy Siêu Thị Lớn', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(8, 'bien-hop-den.jpg', 'Biển Hộp Đèn Siêu Thị', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(9, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Trung Tâm Thương Mại', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(9, 'bien-vay.jpg', 'Biển Vẫy Trung Tâm Thương Mại', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(9, 'bien-hop-den.jpg', 'Biển Hộp Đèn Trung Tâm', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(10, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Khu Công Nghiệp', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(10, 'bien-vay.jpg', 'Biển Vẫy Khu Công Nghiệp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(10, 'bien-hop-den.jpg', 'Biển Hộp Đèn Khu Công Nghiệp', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(11, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Bệnh Viện', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(11, 'bien-vay.jpg', 'Biển Vẫy Bệnh Viện', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(11, 'bien-hop-den.jpg', 'Biển Hộp Đèn Bệnh Viện', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(12, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Trường Học', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(12, 'bien-vay.jpg', 'Biển Vẫy Trường Học', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(12, 'bien-hop-den.jpg', 'Biển Hộp Đèn Trường Học', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(13, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Ngân Hàng', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(13, 'bien-vay.jpg', 'Biển Vẫy Ngân Hàng', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(13, 'bien-hop-den.jpg', 'Biển Hộp Đèn Ngân Hàng', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(14, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Khách Sạn', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(14, 'bien-vay.jpg', 'Biển Vẫy Khách Sạn', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(14, 'bien-hop-den.jpg', 'Biển Hộp Đèn Khách Sạn', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(15, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Resort', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(15, 'bien-vay.jpg', 'Biển Vẫy Resort Cao Cấp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(15, 'bien-hop-den.jpg', 'Biển Hộp Đèn Resort', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(16, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Spa', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(16, 'bien-vay.jpg', 'Biển Vẫy Spa Sang Trọng', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(16, 'bien-hop-den.jpg', 'Biển Hộp Đèn Spa', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(17, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Salon', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(17, 'bien-vay.jpg', 'Biển Vẫy Salon Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(17, 'bien-hop-den.jpg', 'Biển Hộp Đèn Salon', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(18, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Nail', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(18, 'bien-vay.jpg', 'Biển Vẫy Nail Hiện Đại', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(18, 'bien-hop-den.jpg', 'Biển Hộp Đèn Nail', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(19, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Tiệm Rửa Xe', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(19, 'bien-vay.jpg', 'Biển Vẫy Tiệm Rửa Xe', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(19, 'bien-hop-den.jpg', 'Biển Hộp Đèn Tiệm Rửa Xe', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(20, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Garage', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(20, 'bien-vay.jpg', 'Biển Vẫy Garage', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(20, 'bien-hop-den.jpg', 'Biển Hộp Đèn Garage', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(21, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Văn Phòng Công Ty', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(21, 'alu-trong-nha.jpg', 'Biển Alu Trong Nhà Văn Phòng', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(21, 'tấm-nhôm-alu-trong-nhà.jpg', 'Biển Alu Trong Nhà Đẹp', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(22, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Công Ty Xây Dựng', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(22, 'bien-vay.jpg', 'Biển Vẫy Công Ty Xây Dựng', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(22, 'bien-hop-den.jpg', 'Biển Hộp Đèn Công Ty Xây Dựng', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(23, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Công Ty Công Nghệ', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(23, 'bien-vay.jpg', 'Biển Vẫy Công Ty Công Nghệ', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(23, 'bien-hop-den.jpg', 'Biển Hộp Đèn Công Ty Công Nghệ', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(24, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Công Ty Sản Xuất', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(24, 'bien-vay.jpg', 'Biển Vẫy Công Ty Sản Xuất', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(24, 'bien-hop-den.jpg', 'Biển Hộp Đèn Công Ty Sản Xuất', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(25, 'hộp-đèn-siêu-mỏng.jpg', 'Hộp Đèn Mica Siêu Mỏng Hiện Đại', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(25, 'biển-hộp-đèn-mica-siêu-mỏng.jpg', 'Hộp Đèn Mica Siêu Mỏng Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(25, 'hop-den-sieu-mong.jpg', 'Hộp Đèn Siêu Mỏng Hiện Đại', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(25, 'biển-hộp-đèn-led.jpg', 'Hộp Đèn LED Cao Cấp', false, 4, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(26, 'hộp-đèn-hút-nổi.jpg', 'Hộp Đèn Hút Nổi 3D', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(26, 'hop-den-hut-noi.jpg', 'Hộp Đèn Hút Nổi Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(26, 'biển-quảng-cáo-mica-phát-sáng.jpg', 'Hộp Đèn Mica Phát Sáng', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(26, 'bhd-mica-001-1.jpg', 'Hộp Đèn Mica Cao Cấp', false, 4, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(27, 'hop-den-xuyen-sang.jpg', 'Hộp Đèn Xuyên Sáng Hiện Đại', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(27, 'biển-bạt-hiflex-khung-sắt.jpg', 'Hộp Đèn Bạt Hiflex Khung Sắt', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(27, 'bạt-hiflex-xuyên-sáng.jpg', 'Hộp Đèn Bạt Hiflex Xuyên Sáng', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(27, 'bien-bat-hiflex.jpg', 'Hộp Đèn Bạt Hiflex Hiện Đại', false, 4, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(28, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Trung Tâm Hiện Đại', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(28, 'bien-quang-cao-alu-chu-noi-dep-ha-noi-gia-re-scaled.jpg', 'Biển Alu Quảng Cáo Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(28, 'alu-ngoai-troi.jpg', 'Biển Alu Ngoài Trời Cao Cấp', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(29, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Sự Kiện Chuyên Nghiệp', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(29, 'bien-vay.jpg', 'Biển Vẫy Sự Kiện Hiện Đại', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(29, 'bien-vay-quang-cao-la-gi.jpg', 'Biển Vẫy Quảng Cáo Sự Kiện', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(30, 'bien-bat-hiflex.jpg', 'Biển Bạt Hiflex Đường Phố', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(30, 'biển-bạt-hiflex-khung-sắt.jpg', 'Biển Bạt Hiflex Khung Sắt', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(30, 'bạt-hiflex-xuyên-sáng.jpg', 'Biển Bạt Hiflex Xuyên Sáng', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(31, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Trung Tâm Hiện Đại', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(31, 'bien-quang-cao-alu-chu-noi-dep-ha-noi-gia-re-scaled.jpg', 'Biển Alu Quảng Cáo Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(31, 'alu-ngoai-troi.jpg', 'Biển Alu Ngoài Trời Cao Cấp', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(32, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Sự Kiện Chuyên Nghiệp', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(32, 'bien-vay.jpg', 'Biển Vẫy Sự Kiện Hiện Đại', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(32, 'bien-vay-quang-cao-la-gi.jpg', 'Biển Vẫy Quảng Cáo Sự Kiện', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(33, 'bien-bat-hiflex.jpg', 'Biển Bạt Hiflex Đường Phố', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(33, 'biển-bạt-hiflex-khung-sắt.jpg', 'Biển Bạt Hiflex Khung Sắt', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(33, 'bạt-hiflex-xuyên-sáng.jpg', 'Biển Bạt Hiflex Xuyên Sáng', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(34, 'bang-hieu-shop-quan-ao-2.jpg', 'Biển LED Shop Thời Trang Cao Cấp', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(34, 'bien-quang-cao.jpg', 'Biển Quảng Cáo Shop Thời Trang Hiện Đại', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(34, 'bien-vay-quang-cao-la-gi.jpg', 'Biển Vẫy Quảng Cáo Shop Sang Trọng', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(35, 'bang-hieu-pet-shop-thu-cung.jpg', 'Biển LED Pet Shop Hiện Đại', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(35, 'bang-hieu-pet-shop-1-600x600.jpg', 'Biển Pet Shop Đẹp Hiện Đại', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(35, 'bang-hieu-pet-shop-2-600x600.jpg', 'Biển Pet Shop Chuyên Nghiệp', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(35, 'bang-hieu-pet-shop-3-600x600.jpg', 'Biển Pet Shop Thu Cưng', false, 4, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(36, 'biển-quảng-cáo-den-neon.jpg', 'Biển Neon Quán Cafe Vintage', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(36, 'bien-den-led-huynh-quang.jpg', 'Biển Đèn Led Huỳnh Quang Cafe', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(36, 'biển-quảng-cáo-hat-sang.jpg', 'Biển Quảng Cáo Hắt Sáng Cafe', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(37, 'alu-ngoai-troi.jpg', 'Biển Alu Spa Sang Trọng', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(37, 'bien-quang-cao-alu-chu-noi-dep-ha-noi-gia-re-scaled.jpg', 'Biển Alu Quảng Cáo Spa Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(37, 'tấm-nhôm-alu-ngoài-trời.jpg', 'Biển Alu Ngoài Trời Spa', false, 3, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(38, 'biển-quảng-cáo-mica-phát-sáng.jpg', 'Biển Mica Hút Nổi 3D Cao Cấp', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(38, 'bhd-mica-001-1.jpg', 'Biển Mica Phát Sáng Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(38, 'biển-led-mica-cnc.jpg', 'Biển Mica CNC Hiện Đại', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(38, 'biển-quảng-cáo-chữ-noi-dong.jpg', 'Biển Mica Chữ Nổi Đồng', false, 4, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(39, 'biển-quảng-cáo-inox-ma-vang.jpg', 'Biển Inox Vàng Gương Sang Trọng', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(39, 'bcn-inox-vg-001-1.jpg', 'Biển Inox Vàng Gương Cao Cấp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(39, 'biển-inox-đèn-led.jpg', 'Biển Inox Đèn LED Hiện Đại', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(39, 'biển-quảng-cáo-chữ-noi-dong.jpg', 'Biển Inox Chữ Nổi Đồng', false, 4, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(40, 'chữ-nổi-mica-den-led.jpg', 'Chữ Nổi Mica Phát Sáng Ban Đêm', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(40, 'chu-noi-mica-den-led-la-gi.png', 'Chữ Nổi Mica Đèn LED Là Gì', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(40, 'chu-noi-mica-led-hat-sang-mat.png', 'Chữ Nổi Mica LED Hắt Sáng Mặt', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(40, 'chu-noi-mica-led-hat-sang-vien.png', 'Chữ Nổi Mica LED Hắt Sáng Viền', false, 4, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(41, 'chữ-nổi-inox-vàng-gương.jpg', 'Chữ Nổi Inox Vàng Gương Cao Cấp', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(41, 'chu-inox-vang-guong-2.jpg', 'Chữ Nổi Inox Vàng Gương Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(41, 'chu-noi-inox-vang-guong.jpg', 'Chữ Nổi Inox Vàng Gương Sang Trọng', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(41, 'chữ-nổi-inox-phát-sáng.jpg', 'Chữ Nổi Inox Phát Sáng', false, 4, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(42, 'hộp-đèn-siêu-mỏng.jpg', 'Hộp Đèn Mica Siêu Mỏng Hiện Đại', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(42, 'biển-hộp-đèn-mica-siêu-mỏng.jpg', 'Hộp Đèn Mica Siêu Mỏng Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(42, 'hop-den-sieu-mong.jpg', 'Hộp Đèn Siêu Mỏng Hiện Đại', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(42, 'biển-hộp-đèn-led.jpg', 'Hộp Đèn LED Cao Cấp', false, 4, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(43, 'alu-trong-nha.jpg', 'Biển Alu Văn Phòng Chuyên Nghiệp', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(43, 'tấm-nhôm-alu-trong-nhà.jpg', 'Biển Alu Trong Nhà Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(43, 'bien-vay.jpg', 'Biển Alu Văn Phòng Hiện Đại', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(43, 'bien-hop-den.jpg', 'Biển Alu Hộp Đèn Văn Phòng', false, 4, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(44, 'biển-led-ma-tran.jpg', 'Biển LED Ma Trận Nhà Hàng Hiện Đại', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(44, 'bien-led-2.jpg', 'Biển LED Ma Trận Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(44, 'biển-led-chạy-chữ.jpg', 'Biển LED Chạy Chữ Hiện Đại', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(44, 'biển-led-hòa-màu.jpg', 'Biển LED Hòa Màu Cao Cấp', false, 4, NOW())");

                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(45, 'chữ-nổi-led-viền.jpg', 'Chữ Nổi LED Viền Chạy Quanh', true, 1, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(45, 'chu-noi-led-vien.jpg', 'Chữ Nổi LED Viền Đẹp', false, 2, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(45, 'chữ-nổi-led-noi-3d.jpg', 'Chữ Nổi LED 3D Hiện Đại', false, 3, NOW())");
                executeInsert(statement, "INSERT INTO product_images (product_id, image_url, alt_text, is_primary, sort_order, created_at) VALUES " +
                        "(45, 'chữ-nổi-led-viền-màu.jpg', 'Chữ Nổi LED Viền Màu', false, 4, NOW())");

                // =============================================
                // STEP 10: PRODUCT-MATERIAL RELATIONSHIPS (After products and materials)
                // =============================================
                logger.info("Inserting product-material relationships...");

                // Biển Quảng Cáo Tổng Hợp (Category 9)
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(31, 1), (31, 4), (31, 15)"); // Quảng Cáo Trung Tâm - Alu ngoài trời, LED trắng, khung sắt
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(32, 6), (32, 4), (32, 15)"); // Quảng Cáo Sự Kiện - Mica trong suốt, LED trắng, khung sắt
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(33, 11), (33, 15)"); // Quảng Cáo Đường Phố - Bạt hiflex k xuyên sáng, khung sắt

                // Sản phẩm mới từ thư viện hình ảnh
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(34, 4), (34, 5), (34, 15)"); // LED Shop Thời Trang - LED trắng, LED RGB, khung sắt
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(35, 4), (35, 15)"); // LED Pet Shop - LED trắng, khung sắt
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(36, 12), (36, 13), (36, 15)"); // Neon Cafe - Neon đỏ, xanh dương, khung sắt
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(37, 1), (37, 16)"); // Alu Spa - Alu ngoài trời, decal màu
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(38, 6), (38, 15)"); // Mica Hút Nổi - Mica trong suốt, khung sắt
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(39, 8), (39, 16)"); // Inox Vàng Gương - Inox trắng, decal màu
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(40, 6), (40, 4)"); // Chữ Mica Phát Sáng - Mica trong suốt, LED trắng
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(41, 8), (41, 16)"); // Chữ Inox Vàng Gương - Inox trắng, decal màu
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(42, 6), (42, 4), (42, 15)"); // Hộp Đèn Mica - Mica trong suốt, LED trắng, khung sắt
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(43, 2), (43, 16)"); // Alu Văn Phòng - Alu trong nhà, decal màu
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(44, 4), (44, 5), (44, 15)"); // LED Ma Trận - LED trắng, LED RGB, khung sắt
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(45, 4), (45, 5)"); // Chữ LED Viền - LED trắng, LED RGB
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(25, 6), (25, 4), (25, 15)"); // Hộp Đèn Mica Siêu Mỏng - Mica trong suốt, LED trắng, khung sắt
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(26, 6), (26, 4), (26, 15)"); // Hộp Đèn Hút Nổi - Mica trong suốt, LED trắng, khung sắt
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(27, 10), (27, 4), (27, 15)"); // Hộp Đèn Xuyên Sáng - Bạt hiflex xuyên sáng, LED trắng, khung sắt

                // Biển Quảng Cáo Tổng Hợp (Category 9)
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(28, 1), (28, 4), (28, 15)"); // Quảng Cáo Trung Tâm - Alu ngoài trời, LED trắng, khung sắt
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(29, 6), (29, 4), (29, 15)"); // Quảng Cáo Sự Kiện - Mica trong suốt, LED trắng, khung sắt
                executeInsert(statement, "INSERT INTO product_materials (product_id, material_id) VALUES " +
                        "(30, 11), (30, 15)"); // Quảng Cáo Đường Phố - Bạt hiflex k xuyên sáng, khung sắt

                // =============================================
                // STEP 11: INQUIRIES (After contacts and users)
                // =============================================
                /*
                logger.info("Inserting inquiries...");

                executeInsert(statement, "INSERT INTO inquiries (user_id, name, phone, email, address, message, product_id, created_at, status) VALUES " +
                        "(2, 'John Customer', '+1555123456', 'john.customer@example.com', '123 Main St', 'Customer requesting quote for LED business sign. Need to gather requirements and provide pricing.', 1, NOW(), 'NEW')");
                executeInsert(statement, "INSERT INTO inquiries (user_id, name, phone, email, address, message, product_id, created_at, status) VALUES " +
                        "(2, 'Sarah Business Owner', '+1555123457', 'sarah.owner@business.com', '456 Oak Ave', 'Custom neon sign design request. Restaurant owner wants blue neon with specific text. Need to create design mockup.', 4, NOW(), 'IN_PROGRESS')");
                executeInsert(statement, "INSERT INTO inquiries (user_id, name, phone, email, address, message, product_id, created_at, status) VALUES " +
                        "(3, 'Mike Retail Manager', '+1555123458', 'mike.retail@store.com', '789 Pine Ln', 'Maintenance request for existing LED sign. Customer reports flickering. Schedule service call.', 1, NOW(), 'NEW')");
                executeInsert(statement, "INSERT INTO inquiries (user_id, name, phone, email, address, message, product_id, created_at, status) VALUES " +
                        "(2, 'Emily Restaurant Owner', '+1555123459', 'emily.restaurant@food.com', '101 Maple Dr', 'Interested in digital menu board pricing and features. Provide detailed specifications.', 6, NOW(), 'NEW')");
                */

                // =============================================
                // STEP 12: DESIGN TEMPLATES
                // =============================================
                /*
                logger.info("Inserting design templates...");

                executeInsert(statement, "INSERT INTO design_templates (preview_image_url, canvas_template_link, created_at, updated_at) VALUES " +
                        "('templates/modern-business.jpg', 'https://example.com/canvas-template-1', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO design_templates (preview_image_url, canvas_template_link, created_at, updated_at) VALUES " +
                        "('templates/restaurant-special.jpg', 'https://example.com/canvas-template-2', NOW(), NOW())");
                executeInsert(statement, "INSERT INTO design_templates (preview_image_url, canvas_template_link, created_at, updated_at) VALUES " +
                        "('templates/retail-sale.jpg', 'https://example.com/canvas-template-3', NOW(), NOW())");
                */

                // =============================================
                // STEP 13: SITE SETTINGS
                // =============================================
                /*
                logger.info("Inserting site settings...");

                executeInsert(statement, "INSERT INTO site_settings (setting_key, setting_value, description, is_system, created_at, updated_at) VALUES " +
                        "('SITE_NAME', 'SignBoard Manager', 'The name of the signage company website', true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO site_settings (setting_key, setting_value, description, is_system, created_at, updated_at) VALUES " +
                        "('SITE_DESCRIPTION', 'Professional signage solutions for businesses of all sizes', 'Brief description of the company and services', false, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO site_settings (setting_key, setting_value, description, is_system, created_at, updated_at) VALUES " +
                        "('CONTACT_EMAIL', 'info@signboard.com', 'Primary contact email for customer inquiries', false, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO site_settings (setting_key, setting_value, description, is_system, created_at, updated_at) VALUES " +
                        "('CONTACT_PHONE', '+1-800-SIGNAGE', 'Primary contact phone number', false, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO site_settings (setting_key, setting_value, description, is_system, created_at, updated_at) VALUES " +
                        "('CONTACT_ADDRESS', '123 Sign Street, Sign City, SC 12345', 'Physical address of the company', false, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO site_settings (setting_key, setting_value, description, is_system, created_at, updated_at) VALUES " +
                        "('DEFAULT_CURRENCY', 'USD', 'Default currency for pricing display', true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO site_settings (setting_key, setting_value, description, is_system, created_at, updated_at) VALUES " +
                        "('TAX_RATE', '8.5', 'Default tax rate percentage', false, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO site_settings (setting_key, setting_value, description, is_system, created_at, updated_at) VALUES " +
                        "('MAINTENANCE_MODE', 'false', 'Whether the site is in maintenance mode', true, NOW(), NOW())");
                */

                // =============================================
                // STEP 14: BANNERS
                // =============================================
                /*
                logger.info("Inserting banners...");

                executeInsert(statement, "INSERT INTO banners (title, description, image_url, link_url, is_active, start_date, end_date, sort_order, created_at, updated_at) VALUES " +
                        "('Spring Sale - 20% Off LED Signs', 'Limited time offer on all LED signage products', 'banners/spring-led-sale.jpg', '/products?category=led-signage', true, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO banners (title, description, image_url, link_url, is_active, start_date, end_date, sort_order, created_at, updated_at) VALUES " +
                        "('Custom Neon Signs from $299', 'Start your custom neon sign project today', 'banners/neon-custom-offer.jpg', '/contact?subject=custom-neon', true, NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY), 2, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO banners (title, description, image_url, link_url, is_active, start_date, end_date, sort_order, created_at, updated_at) VALUES " +
                        "('Free Installation on Orders Over $1000', 'Professional installation included with qualifying orders', 'banners/free-installation.jpg', '/contact?subject=free-installation', true, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 3, NOW(), NOW())");
                */

                // =============================================
                // STEP 15: ARTICLE CATEGORIES
                // =============================================
                logger.info("Inserting article categories...");

                // Main Categories - Level 0 (Root categories)
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Tin tức công nghệ', 'tin-tuc-cong-nghe', 'Các tin tức mới nhất về công nghệ và kỹ thuật số', true, 0, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Hướng dẫn sử dụng', 'huong-dan-su-dung', 'Hướng dẫn chi tiết cách sử dụng các sản phẩm và dịch vụ', true, 0, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Thông báo', 'thong-bao', 'Các thông báo quan trọng từ công ty', true, 0, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Sự kiện', 'su-kien', 'Thông tin về các sự kiện và hoạt động của công ty', true, 0, 0, NOW(), NOW())");


                // Subcategories - Level 1
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Công nghệ LED', 'cong-nghe-led', 'Tin tức về công nghệ đèn LED mới nhất', 1, true, 1, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Xu hướng thiết kế', 'xu-huong-thiet-ke', 'Các xu hướng thiết kế biển quảng cáo hiện đại', 1, true, 1, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Hướng dẫn lắp đặt', 'huong-dan-lap-dat', 'Hướng dẫn cách lắp đặt các loại biển quảng cáo', 2, true, 1, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Bảo trì và sửa chữa', 'bao-tri-sua-chua', 'Hướng dẫn bảo trì và khắc phục sự cố', 2, true, 1, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Cập nhật sản phẩm', 'cap-nhat-san-pham', 'Thông báo về các sản phẩm mới và cập nhật', 3, true, 1, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Thay đổi chính sách', 'thay-doi-chinh-sach', 'Thông báo về các thay đổi chính sách công ty', 3, true, 1, 0, NOW(), NOW())");


                // Sub-subcategories - Level 2 (Maximum depth)
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('LED ma trận', 'led-ma-tran', 'Công nghệ LED ma trận cho biển quảng cáo động', 5, true, 2, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('LED chạy chữ', 'led-chay-chu', 'Công nghệ LED chạy chữ cho cửa hàng', 5, true, 2, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Thiết kế 3D', 'thiet-ke-3d', 'Xu hướng thiết kế 3D cho biển quảng cáo', 6, true, 2, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Minimalist design', 'minimalist-design', 'Phong cách thiết kế tối giản hiện đại', 6, true, 2, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Hướng dẫn lắp đặt LED', 'huong-dan-lap-dat-led', 'Hướng dẫn chi tiết lắp đặt biển LED', 7, true, 2, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Hướng dẫn lắp đặt neon', 'huong-dan-lap-dat-neon', 'Hướng dẫn lắp đặt biển neon an toàn', 7, true, 2, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Sửa chữa LED', 'sua-chua-led', 'Cách khắc phục sự cố biển LED thường gặp', 8, true, 2, 0, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO article_categories (name, slug, description, parent_id, is_active, level, sort_order, created_at, updated_at) VALUES " +
                        "('Bảo dưỡng neon', 'bao-duong-neon', 'Hướng dẫn bảo dưỡng biển neon định kỳ', 8, true, 2, 0, NOW(), NOW())");

                // =============================================
                // STEP 16: ARTICLES
                // =============================================
                /*
                logger.info("Inserting articles...");

                executeInsert(statement, "INSERT INTO cms_articles (type, title, slug, content, excerpt, featured_image_url, is_featured, created_at, updated_at) VALUES " +
                        "('news', 'Latest Trends in LED Signage Technology', 'latest-led-signage-trends', '<p>The signage industry is constantly evolving with new LED technologies making signs brighter, more efficient, and more customizable than ever before.</p><p>Recent innovations include smart LED modules that can change colors remotely and energy-efficient drivers that reduce power consumption by up to 40%.</p>', 'Discover the latest innovations in LED signage technology that are revolutionizing the industry.', 'articles/led-trends-2024.jpg', true, NOW(), NOW())");
                executeInsert(statement, "INSERT INTO cms_articles (type, title, slug, content, excerpt, featured_image_url, is_featured, created_at, updated_at) VALUES " +
                        "('production_info', 'Choosing the Right Signage for Your Business', 'choosing-right-signage-business', '<p>Selecting the perfect signage solution requires careful consideration of your business type, location, budget, and target audience.</p><p>LED signs work well for high-visibility locations, while neon signs create a classic look for restaurants and bars.</p>', 'Learn how to choose the perfect signage solution for your specific business needs and budget.', 'articles/signage-guide.jpg', false, NOW(), NOW())");
                */

                connection.commit();
                logger.info("Data insertion completed successfully!");

            } catch (SQLException e) {
                connection.rollback();
                logger.error("Error during data insertion, rolling back transaction", e);
                throw e;
            }

        } catch (SQLException e) {
            logger.error("Database connection error", e);
            throw e;
        }
    }

    private void executeInsert(Statement statement, String sql) throws SQLException {
        try {
            statement.execute(sql);
            logger.debug("Executed: {}", sql.substring(0, Math.min(sql.length(), 100)));
        } catch (SQLException e) {
            logger.error("Error executing SQL: {}", sql, e);
            throw e;
        }
    }
}
