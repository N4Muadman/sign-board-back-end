package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.ProductRequest;
import com.techbytedev.signboardmanager.dto.response.MaterialResponse;
import com.techbytedev.signboardmanager.dto.response.ProductResponse;
import com.techbytedev.signboardmanager.entity.*;
import com.techbytedev.signboardmanager.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageRepository productImageRepository;
    private final ProductMaterialRepository productMaterialRepository;
    private final MaterialRepository materialRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, ProductImageRepository productImageRepository, ProductMaterialRepository productMaterialRepository, MaterialRepository materialRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productImageRepository = productImageRepository;
        this.productMaterialRepository = productMaterialRepository;
        this.materialRepository = materialRepository;
        this.categoryRepository = categoryRepository;
    }

    // lấy danh sách sản phẩm
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // thêm sản phẩm
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setSlug(productRequest.getSlug());
        product.setPrice(productRequest.getPrice());
        product.setDiscountPercent(productRequest.getDiscount());
        product.setDiscountedPrice(productRequest.getDiscountPrice());
        product.setDescription(productRequest.getDescription());
        product.setDimensions(productRequest.getDimensions());
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

        for (String imageUrl : productRequest.getImageURLs()) {
            ProductImage productImage = new ProductImage();
            productImage.setProduct(savedProduct);
            productImage.setImageUrl(imageUrl);
            productImageRepository.save(productImage);
        }

        return convertToResponse(savedProduct);
    }
    // sửa sản phẩm
    @Transactional
    public ProductResponse updateProduct(int productId, ProductRequest productRequest) {
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

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        existingProduct.setCategory(category);

        updateMaterials(existingProduct, productRequest.getMaterialIds());
        updateImages(existingProduct, productRequest.getImageURLs());

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
    private void updateImages(Product product, List<String> newImageUrls) {
        List<ProductImage> existingImages = productImageRepository.findByProductId(product.getId());
        Set<String> existingUrls = existingImages.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toSet());

        Set<String> incomingUrls = new HashSet<>(newImageUrls);
        for (ProductImage pi : existingImages) {
            if (!incomingUrls.contains(pi.getImageUrl())) {
                productImageRepository.delete(pi);
            }
        }

        for (String url : incomingUrls) {
            if (!existingUrls.contains(url)) {
                ProductImage newImage = new ProductImage();
                newImage.setProduct(product);
                newImage.setImageUrl(url);
                productImageRepository.save(newImage);
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

        List<String> imageUrls = productImageRepository.findByProductId(product.getId())
                .stream().map(ProductImage::getImageUrl).toList();
        response.setImageURLs(imageUrls);

        List<MaterialResponse> materialResponses = productMaterialRepository.findByProductId(product.getId())
                .stream()
                .map(pm -> new MaterialResponse(pm.getMaterial().getId(), pm.getMaterial().getName()))
                .toList();
        response.setMaterials(materialResponses);

        return response;
    }
    // xóa sản phẩm
    public void deleteProduct (int productId) {
        productRepository.deleteById(productId);
    }
    // hiển thị sản phẩm chi tiết theo id sản phẩm
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
                .filter(image -> image.isPrimary())
                .findFirst()
                .orElse(null);
        if (primaryImage != null) {
            productResponse.setImageURL("/images/" + primaryImage.getImageUrl());
        }

        for (ProductImage productImage : productImages) {
            productResponse.getImageURLs().add("/images/" + productImage.getImageUrl());
        }
        List<ProductMaterial> productMaterials =
                productMaterialRepository.findByProductId(productId);
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