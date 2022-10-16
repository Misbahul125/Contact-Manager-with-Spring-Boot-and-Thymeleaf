package com.contactmanager.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contactmanager.models.Contact;
import com.contactmanager.repos.ContactRepository;
import com.contactmanager.repos.UserRepository;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	@GetMapping("/search/{searchKey}")
	public ResponseEntity<?> searchContacts(
			@PathVariable("searchKey") String searchKey,
			Principal principal
	) {
		
		System.out.println(searchKey);
		
		List<Contact> contacts = this.contactRepository.searchByFullNameContainingAndUser(searchKey, this.userRepository.getUserByUserName(principal.getName()));
		
		return ResponseEntity.ok(contacts);
		
	}
	
}
