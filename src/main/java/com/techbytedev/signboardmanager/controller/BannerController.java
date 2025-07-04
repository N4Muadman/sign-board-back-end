package com.techbytedev.signboardmanager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<Banner> getAllBanners() {
        return bannerService.getAllBanners();
    }

    //get banner by id
    @GetMapping("/{id}")
    public Banner getBannerById(Long id) {
        return bannerService.getBannerById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with id: " + id));
    }

    //create banner
@PostMapping
    public Banner createBanner(@RequestBody Banner banner) {
        return bannerService.createBanner(banner);
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
