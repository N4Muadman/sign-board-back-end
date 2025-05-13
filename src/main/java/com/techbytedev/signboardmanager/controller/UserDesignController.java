package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.entity.UserDesign;
import com.techbytedev.signboardmanager.service.UserDesignService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-designs")
public class UserDesignController {

    private final UserDesignService userDesignService;

    public UserDesignController(UserDesignService userDesignService) {
        this.userDesignService = userDesignService;
    }

    @PostMapping
    public ResponseEntity<UserDesign> taoThietKeNguoiDung(@RequestBody @Valid UserDesign userDesign) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userDesignService.tao(userDesign));
    }

    @GetMapping
    public ResponseEntity<List<UserDesign>> layTatCaThietKeNguoiDung() {
        return ResponseEntity.ok(userDesignService.layTatCa());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDesign> layThietKeTheoId(@PathVariable("id") Long id) {
        UserDesign userDesign = userDesignService.layTheoId(id);
        return userDesign != null ? ResponseEntity.ok(userDesign) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDesign> capNhatThietKeNguoiDung(@PathVariable("id") Long id, @RequestBody @Valid UserDesign userDesign) {
        userDesign.setId(id);
        UserDesign updatedDesign = userDesignService.capNhat(userDesign);
        return updatedDesign != null ? ResponseEntity.ok(updatedDesign) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaThietKeNguoiDung(@PathVariable("id") Long id) {
        if (userDesignService.xoa(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/{designId}/templates")
    public ResponseEntity<Void> ganMauThietKe(@PathVariable("designId") Long designId, @RequestBody List<Long> templateIds) {
        userDesignService.ganMauThietKe(designId, templateIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{designId}/templates")
    public ResponseEntity<Void> goMauThietKe(@PathVariable("designId") Long designId, @RequestBody List<Long> templateIds) {
        userDesignService.goMauThietKe(designId, templateIds);
        return ResponseEntity.ok().build();
    }
}