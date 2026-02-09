package com.techbytedev.signboardmanager.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techbytedev.signboardmanager.dto.request.BannerRequest;
import com.techbytedev.signboardmanager.entity.Banner;
import com.techbytedev.signboardmanager.service.BannerService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/banners")
public class BannerController {
    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    //get all banners
 @GetMapping
public Page<Banner> getAllBanners(
        @PageableDefault(size = 10, sort = "id") Pageable pageable) {
    return bannerService.getAllBanners(pageable);
}

    //get banner by id
    @GetMapping("/{id}")
    public Banner getBannerById(Long id) {
        return bannerService.getBannerById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with id: " + id));
    }

    //create banner
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Banner> createBanner(@ModelAttribute BannerRequest bannerRequest) {
        Banner banner = bannerService.createBannerFromRequest(bannerRequest);
        return ResponseEntity.ok(banner);
    }

    //update banner
    @PutMapping("/{id}")
    public Banner updateBanner(@PathVariable Long id, @RequestBody Banner bannerDetails) {
        return bannerService.updateBanner(id, bannerDetails);
    }

    //delete banner
    @DeleteMapping("/{id}")
    public void deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
    }
    

}
