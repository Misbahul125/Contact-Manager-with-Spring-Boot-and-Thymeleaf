package com.contactmanager.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactmanager.models.Contact;
import com.contactmanager.models.User;
import com.contactmanager.repos.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {

	private User user = null;

	@Autowired
	private UserRepository userRepository;

	// to be automatically called beforea controller perform its action
	@ModelAttribute
	public void getUserData(Model model, Principal principal) {

		user = userRepository.getUserByUserName(principal.getName());

		model.addAttribute("user", user);

	}

	// view URLs

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

	// processing URLs

	@PostMapping("/add-contact-action")
	public String addContactAction(@ModelAttribute("contact") Contact contact,
			@RequestParam("cImage") MultipartFile multipartFile) {

		try {
			// System.out.println(contact);

			if (multipartFile.isEmpty()) {

				System.out.println("File is empty");

			} else {

				String originalName = multipartFile.getOriginalFilename();
				String fName = originalName.substring(0 , originalName.lastIndexOf(".")) + "_" +System.currentTimeMillis() + originalName.substring(originalName.lastIndexOf("."));
				
				if(isImageUploadSuccessful(multipartFile, fName)) {
					contact.setImage(fName);
				}
				
			}
			
			contact.setUser(user);
			user.getContacts().add(contact);

			this.userRepository.save(user);

			System.out.println("Contact added successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "/normal/add-contact";

	}

	public boolean isImageUploadSuccessful(MultipartFile multipartFile , String fName) {

		try {
			File file = new ClassPathResource("static/images").getFile();

			Path path = Paths.get(file.getAbsolutePath() + File.separator + fName);

			Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

}
