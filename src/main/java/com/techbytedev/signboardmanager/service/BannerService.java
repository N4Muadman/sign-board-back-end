package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.request.BannerRequest;
import com.techbytedev.signboardmanager.entity.Banner;
import com.techbytedev.signboardmanager.entity.Product;
import com.techbytedev.signboardmanager.repository.BannerRepository;
import com.techbytedev.signboardmanager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
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
public Banner createBannerFromRequest(BannerRequest bannerRequest) {
    Banner banner = new Banner();
    banner.setTitle(bannerRequest.getTitle());
    banner.setDescription(bannerRequest.getDescription());
    banner.setActive(bannerRequest.isActive());

    try {
        byte[] imageBytes = bannerRequest.getImage().getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        banner.setImageBase64(base64Image);
    } catch (Exception e) {
        throw new RuntimeException("Failed to convert image to Base64", e);
    }

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
        try
        {
            Banner banner = bannerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Banner not found with id: " + id));
            bannerRepository.delete(banner);
        } catch (RuntimeException e) {
            throw new RuntimeException("Banner not found with id: " + id, e);
        }

                
               
        
    }

    public Page<Banner> getAllBanners(Pageable pageable) {
    return bannerRepository.findAll(pageable);
}

   
}
