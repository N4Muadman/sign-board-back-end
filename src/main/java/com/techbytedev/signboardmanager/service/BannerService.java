package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.entity.Banner;
import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.repository.BannerRepository;
import com.techbytedev.signboardmanager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public Optional<Banner> getBannerById(Long id) {
        return bannerRepository.findById(id);
    }

    @Transactional
    public Banner createBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    @Transactional
    public Banner updateBanner(Long id, Banner bannerDetails) {
        Banner existingBanner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with id: " + id));

        existingBanner.setTitle(bannerDetails.getTitle());
        existingBanner.setImageBase64(bannerDetails.getImageBase64());
        existingBanner.setDescription(bannerDetails.getDescription());
        existingBanner.setActive(bannerDetails.isActive());

        return bannerRepository.save(existingBanner);
    }

    @Transactional
    public void deleteBanner(Long id) {
        Banner existingBanner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with id: " + id));
        bannerRepository.delete(existingBanner);
    }
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

   
}