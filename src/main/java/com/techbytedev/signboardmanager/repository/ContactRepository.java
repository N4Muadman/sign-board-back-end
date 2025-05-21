package com.techbytedev.signboardmanager.repository;

import com.techbytedev.signboardmanager.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    List<Contact> findByProductId(int productId);
    Page<Contact> findAll(Pageable pageable);
}
