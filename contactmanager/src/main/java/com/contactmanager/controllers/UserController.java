package com.contactmanager.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contactmanager.models.Contact;
import com.contactmanager.models.User;
import com.contactmanager.repos.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private User user = null;
	
	@Autowired
	private UserRepository userRepository;
	
	//to be automatically called beforea controller perform its action
	@ModelAttribute
	public void getUserData(Model model, Principal principal) {
		
		user = userRepository.getUserByUserName(principal.getName());
		
		model.addAttribute("user", user);
		
	}
	
	//view URLs

	@GetMapping("/index")
	public String dashboard(Model model) {
		
		model.addAttribute("title", "Dashboard");
		
		return "normal/user_dashboard";
	}
	
	@GetMapping("/add-contact")
	public String addContact(Model model) {
		
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add-contact";
	}
	
	
	
	//processing URLs
	
	@PostMapping("/add-contact-action")
	public String addContactAction(
			@ModelAttribute("contact") Contact contact
	) {
		
		//System.out.println(contact);
		
		contact.setUser(user);
		user.getContacts().add(contact);
		
		this.userRepository.save(user);
		
		System.out.println("Contact added successfully");
		
		return "/normal/add-contact";
		
	}
	
}
