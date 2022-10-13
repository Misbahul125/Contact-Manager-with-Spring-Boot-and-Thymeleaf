package com.contactmanager.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactmanager.models.Contact;
import com.contactmanager.models.User;
import com.contactmanager.repos.ContactRepository;
import com.contactmanager.repos.UserRepository;
import com.contactmanager.utils.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	private User user = null;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	// to be automatically called before each controller perform its action
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
	public String addContactAction(
			@ModelAttribute("contact") Contact contact,
			@RequestParam("cImage") MultipartFile multipartFile,
			HttpSession httpSession
	) {

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
				else {
					httpSession.setAttribute("message", new Message("Oops! Something went wrong, unable to upload image." , "alert-danger"));
					return "/normal/add-contact";
				}
				
			}
			
			contact.setUser(user);
			user.getContacts().add(contact);

			this.userRepository.save(user);
			
			httpSession.setAttribute("message", new Message("Contact added successfully !!" , "alert-success"));

			System.out.println("Contact added successfully");

		} catch (Exception e) {
			e.printStackTrace();
			httpSession.setAttribute("message", new Message("Oops! Something went wrong, unable to upload contact." , "alert-danger"));
		}

		return "/normal/add-contact";

	}
	
	@GetMapping("/view-contacts/{page}")
	public String getContacts(
			Model model,
			@PathVariable("page") int page
	) {
		
		Pageable pageable = PageRequest.of(page, 2);
		
		Page<Contact> pagedContacts = this.contactRepository.getContactsByUserId(user.getId() , pageable);

		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", pagedContacts.getTotalPages());
		model.addAttribute("pagedContacts", pagedContacts);
		model.addAttribute("title", "View Contacts");

		return "normal/view-contacts";
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
