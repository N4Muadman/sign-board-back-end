package com.techbytedev.signboardmanager.service;

import com.techbytedev.signboardmanager.dto.response.ContactResponse;
import com.techbytedev.signboardmanager.entity.Contact;
import com.techbytedev.signboardmanager.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }

    public List<ContactResponse> getReviewsByProductId (int productId) {
        List<Contact> contacts = contactRepository.findByProductId(productId);
        List<ContactResponse> contactList = new ArrayList<>();
        for (Contact contact : contacts) {
            ContactResponse contactResponse = new ContactResponse();
            contactResponse.setId(contact.getId());
            contactResponse.setName(contact.getName());
            contactResponse.setEmail(contact.getEmail());
            contactResponse.setRate(contact.getRating());
            contactResponse.setMessage(contact.getMessage());
            contactResponse.setCreatedAt(contact.getCreatedAt());
            contactList.add(contactResponse);
        }
        return contactList;
    }
}
