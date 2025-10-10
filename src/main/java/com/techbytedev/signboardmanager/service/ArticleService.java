package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.ArticleRequest;
import com.techbytedev.signboardmanager.entity.Article;
import com.techbytedev.signboardmanager.entity.PostType;
import com.techbytedev.signboardmanager.entity.ArticleCategory;
import com.techbytedev.signboardmanager.repository.ArticleRepository;
import com.techbytedev.signboardmanager.repository.ArticleCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class ArticleService {

    private final FileStorageService fileStorageService;
    private final ArticleRepository articleRepository;
    private final ArticleCategoryRepository articleCategoryRepository;

    // Hàm tạo slug từ title
    private String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "untitled";
        }

        // Chuyển về chữ thường và loại bỏ dấu tiếng Việt
        String normalized = title.toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y");

        // Thay thế khoảng trắng và ký tự đặc biệt bằng dấu gạch ngang
        String slug = normalized.replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        return slug.isEmpty() ? "untitled" : slug;
    }

    public ArticleService(ArticleRepository articleRepository,
                         ArticleCategoryRepository articleCategoryRepository,
                         FileStorageService fileStorageService) {
        this.articleRepository = articleRepository;
        this.articleCategoryRepository = articleCategoryRepository;
        this.fileStorageService = fileStorageService;
    }

    public Page<Article> getAllArticles(Pageable pageable, String style) {
    if (style == null || style.isEmpty()) {
        return articleRepository.findAll(pageable);
    }
    try {
        return articleRepository.findAllByTypeOrderByCreatedAtDesc(pageable, PostType.valueOf(style));
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Loại bài viết không hợp lệ: " + style);
    }
}

    public Article createArticleFromDTO(ArticleRequest dto, MultipartFile imageFile) throws IOException {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setExcerpt(dto.getExcerpt());
        article.setFeatured(dto.isFeatured());

        // Set category và type dựa trên category name
        if (dto.getCategoryId() != null) {
            ArticleCategory category = articleCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục bài viết không tồn tại"));
            article.setCategory(category);

            // Chỉ thiết lập type nếu tên danh mục hợp lệ với enum
            try {
                String formattedType = category.getName().toLowerCase().replaceAll("\\s+", "_");
                article.setType(PostType.valueOf(formattedType));
            } catch (IllegalArgumentException e) {
                // Nếu tên danh mục không hợp lệ với enum, thiết lập mặc định là news
                article.setType(PostType.news);
                System.out.println("Invalid category name for enum: " + category.getName() + ", using default news type");
            }
        } else {
            // Nếu không có category, thiết lập type mặc định là news (để tương thích ngược)
            article.setType(PostType.news);
        }

        // Tự động tạo slug từ title
        String slug = generateSlug(dto.getTitle());
        article.setSlug(slug);

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            article.setFeaturedImageUrl(fileName);

            // Lưu thêm ảnh dạng base64
            String base64 = Base64.getEncoder().encodeToString(imageFile.getBytes());
            article.setImageBase64(base64);
        }

        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    public Article updateArticle(int id, ArticleRequest dto, MultipartFile imageFile) throws IOException {
    Article existingArticle = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

    existingArticle.setTitle(dto.getTitle());
    existingArticle.setContent(dto.getContent());
    existingArticle.setExcerpt(dto.getExcerpt());
    existingArticle.setFeatured(dto.isFeatured());

    // Set category và type dựa trên category name
    if (dto.getCategoryId() != null) {
        ArticleCategory category = articleCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Danh mục bài viết không tồn tại"));
        existingArticle.setCategory(category);

        // Chỉ thiết lập type nếu tên danh mục hợp lệ với enum
        try {
            String formattedType = category.getName().toLowerCase().replaceAll("\\s+", "_");
            existingArticle.setType(PostType.valueOf(formattedType));
        } catch (IllegalArgumentException e) {
            // Nếu tên danh mục không hợp lệ với enum, thiết lập mặc định là news
            existingArticle.setType(PostType.news);
            System.out.println("Invalid category name for enum: " + category.getName() + ", using default news type");
        }
    } else if (existingArticle.getCategory() == null) {
        // Chỉ thiết lập type mặc định nếu không có category nào cả (để tương thích ngược)
        existingArticle.setType(PostType.news);
    }

    // Cập nhật slug nếu title thay đổi
    if (!dto.getTitle().equals(existingArticle.getTitle())) {
        String newSlug = generateSlug(dto.getTitle());
        existingArticle.setSlug(newSlug);
    }

    if (imageFile != null && !imageFile.isEmpty()) {
        // Xóa ảnh cũ nếu có
        if (existingArticle.getFeaturedImageUrl() != null) {
            fileStorageService.deleteFile(existingArticle.getFeaturedImageUrl());
        }
        // Lưu ảnh mới
        String fileName = fileStorageService.saveFile(imageFile);
        existingArticle.setFeaturedImageUrl(fileName);

        // Lưu thêm ảnh dạng base64
        String base64 = Base64.getEncoder().encodeToString(imageFile.getBytes());
        existingArticle.setImageBase64(base64);
    }

    existingArticle.setUpdatedAt(LocalDateTime.now());
    return articleRepository.save(existingArticle);
}

    public void deleteArticle(int id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        // Xóa ảnh nếu có
        if (article.getFeaturedImageUrl() != null) {
            try {
                fileStorageService.deleteFile(article.getFeaturedImageUrl());
            } catch (IOException e) {
                // Log lỗi nhưng không làm gián đoạn
            }
        }

        articleRepository.deleteById(id);
    }

    public List<Article> searchArticles(String keyword) {
        return articleRepository.findByTitleContainingOrContentContaining(keyword, keyword);
    }

    public List<Article> getFeaturedProjects() {
        List<Article> articles = articleRepository.findByTypeAndIsFeaturedTrueOrderByCreatedAtDesc(PostType.project);
        for (Article article : articles) {
            if (article.getFeaturedImageUrl() != null) {
                article.setFeaturedImageUrl(article.getFeaturedImageUrl());
            }
        }
        return articles;
    }

    public List<Article> getProductionArticles() {
        List<Article> articles = articleRepository.findByType(PostType.production_info);
        for (Article article : articles) {
            if (article.getFeaturedImageUrl() != null) {
                article.setFeaturedImageUrl( article.getFeaturedImageUrl());
            }
        }
        return articles;
    }

    public List<Article> getNewsArticlesSortedByTime() {
        List<Article> articles = articleRepository.findByTypeOrderByCreatedAtDesc(PostType.news);
        for (Article article : articles) {
            if (article.getFeaturedImageUrl() != null) {
                article.setFeaturedImageUrl(  article.getFeaturedImageUrl());
            }
        }
        return articles;
    }

    public Article getArticleById(int id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));
    }

    public Article getArticleBySlug(String slug) {
        return articleRepository.findBySlug(slug);
    }

    // Lấy bài viết theo category ID
    public List<Article> getArticlesByCategoryId(int categoryId) {
        List<Article> articles = articleRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);
        for (Article article : articles) {
            if (article.getFeaturedImageUrl() != null) {
                article.setFeaturedImageUrl(article.getFeaturedImageUrl());
            }
        }
        return articles;
    }

    // Lấy bài viết theo category slug hoặc subcategory slug
    public List<Article> getArticlesByCategorySlug(String categorySlug, String subcategorySlug) {
        ArticleCategory category = null;

        if (subcategorySlug != null && !subcategorySlug.isEmpty()) {
            // Nếu có subcategory slug, tìm subcategory trước
            ArticleCategory subcategory = articleCategoryRepository.findBySlugAndIsActiveTrue(subcategorySlug);
            if (subcategory != null) {
                category = subcategory;
            }
        } else {
            // Nếu không có subcategory, tìm category chính
            category = articleCategoryRepository.findBySlugAndIsActiveTrue(categorySlug);
        }

        if (category != null) {
            return getArticlesByCategoryId(category.getId());
        }

        return List.of(); // Trả về danh sách rỗng nếu không tìm thấy category
    }
}
