package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.ProductRequest;
import com.techbytedev.signboardmanager.dto.response.MaterialResponse;
import com.techbytedev.signboardmanager.dto.response.ProductResponse;
import com.techbytedev.signboardmanager.entity.*;
import com.techbytedev.signboardmanager.repository.*;

import org.slf4j.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageRepository productImageRepository;
    private final ProductMaterialRepository productMaterialRepository;
    private final MaterialRepository materialRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository, ProductMapper productMapper,
            ProductImageRepository productImageRepository,
            ProductMaterialRepository productMaterialRepository,
            MaterialRepository materialRepository, CategoryRepository categoryRepository,
            FileStorageService fileStorageService,
            CategoryService categoryService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productImageRepository = productImageRepository;
        this.productMaterialRepository = productMaterialRepository;
        this.materialRepository = materialRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
        this.categoryService = categoryService;
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Transactional
    public Page<Product> getProductsByCategoryId(int categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    public ProductResponse createProduct(ProductRequest productRequest, List<MultipartFile> imageFiles)
            throws IOException {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setSlug(productRequest.getSlug());
        product.setPrice(productRequest.getPrice());
        product.setDiscountPercent(productRequest.getDiscount());
        product.setDiscountedPrice(productRequest.getDiscountPrice());
        product.setDescription(productRequest.getDescription());
        product.setDimensions(productRequest.getDimensions());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);

        for (Integer materialId : productRequest.getMaterialIds()) {
            Material material = materialRepository.findById(materialId)
                    .orElseThrow(() -> new RuntimeException("Material not found"));
            ProductMaterial productMaterial = new ProductMaterial();
            productMaterial.setProduct(savedProduct);
            productMaterial.setMaterial(material);
            productMaterialRepository.save(productMaterial);
        }

        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile imageFile : imageFiles) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    String fileName = fileStorageService.saveFile(imageFile);
                    if (fileName != null) {
                        ProductImage productImage = new ProductImage();
                        productImage.setProduct(savedProduct);
                        productImage.setImageUrl(fileName);
                        productImage.setCreatedAt(LocalDateTime.now());
                        String base64 = Base64.getEncoder().encodeToString(imageFile.getBytes());
                        productImage.setImageBase64(base64);
                        productImageRepository.save(productImage);
                    }
                }
            }
        }

        return convertToResponse(savedProduct);
    }

    @Transactional
    public Page<Product> getProductsByCategoryAndSubcategories(int categoryId, Pageable pageable) {
        List<Integer> categoryIds = categoryService.getCategoryAndSubcategoryIds(categoryId);
        return productRepository.findByCategoryIdIn(categoryIds, pageable);
    }

    @Transactional
    public ProductResponse updateProduct(int productId, ProductRequest productRequest, List<MultipartFile> imageFiles)
            throws IOException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setDiscountPercent(productRequest.getDiscount());
        existingProduct.setDiscountedPrice(productRequest.getDiscountPrice());
        existingProduct.setActive(true);
        existingProduct.setDimensions(productRequest.getDimensions());
        existingProduct.setSlug(productRequest.getSlug());
        existingProduct.setUpdatedAt(LocalDateTime.now());
                logger.info("keptImageUrls: {}", productRequest.getKeptImageUrls());
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        existingProduct.setCategory(category);

        updateMaterials(existingProduct, productRequest.getMaterialIds());
        updateImages(existingProduct, imageFiles, productRequest.getKeptImageUrls());

        return convertToResponse(existingProduct);
    }

    private void updateMaterials(Product product, List<Integer> newMaterialIds) {
        List<ProductMaterial> existingLinks = productMaterialRepository.findByProductId(product.getId());
        Set<Integer> existingIds = existingLinks.stream()
                .map(pm -> pm.getMaterial().getId())
                .collect(Collectors.toSet());

        Set<Integer> incomingIds = new HashSet<>(newMaterialIds);

        for (ProductMaterial pm : existingLinks) {
            if (!incomingIds.contains(pm.getMaterial().getId())) {
                productMaterialRepository.delete(pm);
            }
        }
        for (Integer materialId : incomingIds) {
            if (!existingIds.contains(materialId)) {
                Material material = materialRepository.findById(materialId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy vật liệu"));
                ProductMaterial newLink = new ProductMaterial();
                newLink.setProduct(product);
                newLink.setMaterial(material);
                productMaterialRepository.save(newLink);
            }
        }
    }

    private void updateImages(Product product, List<MultipartFile> imageFiles, List<String> keptImageUrls) throws IOException {
        List<ProductImage> existingImages = product.getImages();

        if (existingImages != null && keptImageUrls != null) {
            Iterator<ProductImage> iterator = existingImages.iterator();
            while (iterator.hasNext()) {
                ProductImage pi = iterator.next();
                if (!keptImageUrls.contains(pi.getImageUrl())) {
                    fileStorageService.deleteFile(pi.getImageUrl());
                    iterator.remove();
                }
            }
        }

        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile imageFile : imageFiles) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    String fileName = fileStorageService.saveFile(imageFile);
                    if (fileName != null) {
                        ProductImage newImage = new ProductImage();
                        newImage.setProduct(product);
                        newImage.setImageUrl(fileName);
                        newImage.setCreatedAt(LocalDateTime.now());
                        String base64 = Base64.getEncoder().encodeToString(imageFile.getBytes());
                        newImage.setImageBase64(base64);
                        product.getImages().add(newImage);
                    }
                }
            }
        }
    }

    public ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setDiscount(product.getDiscountPercent());
        response.setDiscountPrice(product.getDiscountedPrice());
        response.setDescription(product.getDescription());
        response.setDimensions(product.getDimensions());

        List<ProductImage> productImages = productImageRepository.findByProductId(product.getId());
        List<String> imageUrls = productImages.stream()
                .map(ProductImage::getImageUrl)
                .toList();
        Map<String, String> imageBase64Map = productImages.stream()
                .filter(pi -> pi.getImageBase64() != null)
                .collect(Collectors.toMap(
                        ProductImage::getImageUrl,
                        ProductImage::getImageBase64,
                        (existing, replacement) -> existing // Handle duplicates (unlikely)
                ));
        response.setImageURLs(imageUrls);
        response.setImageBase64Map(imageBase64Map);

        List<MaterialResponse> materialResponses = productMaterialRepository.findByProductId(product.getId())
                .stream()
                .map(pm -> new MaterialResponse(pm.getMaterial().getId(), pm.getMaterial().getName()))
                .toList();
        response.setMaterials(materialResponses);

        return response;
    }

    public void deleteProduct(int productId) {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        for (ProductImage image : images) {
            try {
                fileStorageService.deleteFile(image.getImageUrl());
            } catch (IOException e) {
                logger.warn("Failed to delete image file: {}", image.getImageUrl(), e);
            }
            productImageRepository.delete(image);
        }
        productRepository.deleteById(productId);
    }

    public ProductResponse getProductDetailsById(int productId) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (!productOpt.isPresent()) {
            return null;
        }
        Product product = productOpt.get();
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setPrice(product.getPrice());
        productResponse.setDiscount(product.getDiscountPercent());
        productResponse.setDiscountPrice(product.getDiscountedPrice());
        productResponse.setDescription(product.getDescription());
        productResponse.setDimensions(product.getDimensions());

        ProductImage primaryImage = productImages.stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .orElse(null);
        if (primaryImage != null) {
            productResponse.setImageURL("/images/" + primaryImage.getImageUrl());
        }

        Map<String, String> imageBase64Map = productImages.stream()
                .filter(pi -> pi.getImageBase64() != null)
                .collect(Collectors.toMap(
                        pi -> "/images/" + pi.getImageUrl(),
                        ProductImage::getImageBase64,
                        (existing, replacement) -> existing // Handle duplicates
                ));

        for (ProductImage productImage : productImages) {
            productResponse.getImageURLs().add("/images/" + productImage.getImageUrl());
        }
        productResponse.setImageBase64Map(imageBase64Map);

        List<ProductMaterial> productMaterials = productMaterialRepository.findByProductId(productId);
        List<MaterialResponse> materialResponses = new ArrayList<>();

        for (ProductMaterial productMaterial : productMaterials) {
            Material material = productMaterial.getMaterial();
            MaterialResponse materialResponse = new MaterialResponse();
            materialResponse.setId(material.getId());
            materialResponse.setName(material.getName());
            materialResponse.setDescription(material.getDescription());
            materialResponses.add(materialResponse);
        }

        productResponse.setMaterials(materialResponses);
        return productResponse;
    }

    public List<ProductResponse> filterProducts(String name, Integer categoryId,
            BigDecimal minPrice, BigDecimal maxPrice,
            String sort) {
        List<Product> products = productRepository.findAll();
        if (name != null && !name.trim().isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (categoryId != null) {
            products = products.stream()
                    .filter(p -> p.getCategory().getId() == categoryId)
                    .collect(Collectors.toList());
        }
        if (minPrice != null) {
            products = products.stream()
                    .filter(p -> p.getDiscountedPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }

        if (maxPrice != null) {
            products = products.stream()
                .filter(p -> p.getDiscountedPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());
        }
        if (sort.equalsIgnoreCase("asc")) {
            products.sort(Comparator.comparing(Product::getDiscountedPrice));
        } else if (sort.equalsIgnoreCase("desc")) {
            products.sort(Comparator.comparing(Product::getDiscountedPrice).reversed());
        }

        return productMapper.toProductResponseList(products);
    }
}