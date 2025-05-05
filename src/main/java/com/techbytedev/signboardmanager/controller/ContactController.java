package com.techbytedev.signboardmanager.controller;

import com.techbytedev.signboardmanager.dto.response.ContactResponse;
import com.techbytedev.signboardmanager.entity.Contact;
import com.techbytedev.signboardmanager.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    //ADMIN
    // danh sách liên hệ
    @GetMapping("/list")
    public ResponseEntity<List<Contact>> getList(){
        return ResponseEntity.ok(contactService.getAllContacts());
    }
    //CUSTOMER
    // hiển thị đánh giá theo id của sản phẩm
    @GetMapping("/{productId}")
    public List<ContactResponse> getReviewsByProductId(@PathVariable int productId){
        return contactService.getReviewsByProductId(productId);
    }
    
}
