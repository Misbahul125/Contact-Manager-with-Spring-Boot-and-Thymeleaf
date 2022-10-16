package com.contactmanager.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

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
import com.contactmanager.utils.ImageHelper;
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
	
	@PostMapping("/update-contact/{contactId}")
	public String updateContact(
			Model model,
			@PathVariable("contactId") int contactId
	) {

		Contact contact = this.contactRepository.findById(contactId).get();
		
		model.addAttribute("title", "Update Contact");
		model.addAttribute("contact", contact);

		return "normal/update-contact";
	}

	@GetMapping("/view-profile")
	public String viewProfile(Model model) {
		
		model.addAttribute("title", "My Profile");
		
		return "/normal/view-profile";
		
	}
	
	// processing URLs

	@PostMapping("/add-contact-action")
	public String addContactAction(
			@ModelAttribute("contact") Contact contact,
			@RequestParam("cImage") MultipartFile multipartFile,
			HttpSession httpSession
	) {

		try {

			if (multipartFile.isEmpty()) {
				
				contact.setImage("user.png");

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
			
			contact.setFullName(contact.getFirstName()+" "+contact.getSecondName());
			
			contact.setUser(user);
			user.getContacts().add(contact);

			this.userRepository.save(user);
			
			httpSession.setAttribute("message", new Message("Contact added successfully !!" , "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			httpSession.setAttribute("message", new Message("Oops! Something went wrong, unable to upload contact." , "alert-danger"));
		}

		return "/normal/add-contact";

	}
	
	@GetMapping("/view-contacts/{page}")
	public String getContacts(
			Model model,
			@PathVariable("page") int page,
			HttpSession httpSession
	) {
		
		Pageable pageable = PageRequest.of(page, 2);
		
		Page<Contact> pagedContacts = this.contactRepository.getContactsByUserId(user.getId() , pageable);
		
		if(pagedContacts != null || pagedContacts.isEmpty()) {
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", pagedContacts.getTotalPages());
			model.addAttribute("pagedContacts", pagedContacts);
			
		}
		else {
			httpSession.setAttribute("message", new Message("Oops! There is/are not contact(s) to view. Add one to see." , "alert-danger"));
		}

		model.addAttribute("title", "View Contacts");

		return "normal/view-contacts";
	}
	
	@GetMapping("/contact-detail/{contactId}")
	public String viewContact(
			Model model,
			@PathVariable("contactId") int contactId,
			HttpSession httpSession
	) {
		
		Contact contact = this.contactRepository.findById(contactId).get();
		
		if(user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}
		else {
			httpSession.setAttribute("message", new Message("Sorry! You are not authorized to view this particular contact." , "alert-danger"));
		}
		
		model.addAttribute("title", "Contact Detail");

		return "normal/contact-detail";
	}
	
	@PostMapping("/update-contact-action")
	public String updateContactAction(
			@ModelAttribute("contact") Contact contact,
			@RequestParam("cImage") MultipartFile multipartFile,
			HttpSession httpSession
	) {

		try {
			
			System.out.println(contact);
			
			Contact originalContact = this.contactRepository.findById(contact.getContactId()).get();
			
			String oldImage = originalContact.getImage();

			originalContact = contact;
			
			  if (!multipartFile.isEmpty()) {
				  
				  if(oldImage != null && !oldImage.isEmpty() && !oldImage.matches("user.png")) {
					  
					  if(!ImageHelper.deleteImage(originalContact.getImage())) {
						  
						  System.out.println("Image file delete failed!!");
						  httpSession.setAttribute("message", new Message("Oops! Something went wrong, unable to update contact." , "alert-danger"));
						  return "/normal/update-contact";  
						  
					  }
					  
				  }
				  
				  String originalName = multipartFile.getOriginalFilename(); 
				  String fName = originalName.substring(0 , originalName.lastIndexOf(".")) + "_" +System.currentTimeMillis() + originalName.substring(originalName.lastIndexOf("."));
		  
				  if(isImageUploadSuccessful(multipartFile, fName)) {
					  originalContact.setImage(fName); 
				  }
				  else {
					  System.out.println("Image file write failed!!");
					  httpSession.setAttribute("message", new Message("Oops! Something went wrong, unable to update contact." , "alert-danger"));
					  return "/normal/update-contact";
			  	  }
			  
			  }
			  else {
				  originalContact.setImage(oldImage);
			  }
			  
			  originalContact.setUser(user);
			  
			  this.contactRepository.save(originalContact);
			  
			  httpSession.setAttribute("message", new Message("Contact updated successfully !!" , "alert-success"));
			  
			  return "redirect:/user/contact-detail/"+originalContact.getContactId();
			 

		} catch (Exception e) {
			e.printStackTrace();
			httpSession.setAttribute("message", new Message("Oops! Something went wrong, unable to upload contact." , "alert-danger"));
			return "/normal/update-contact";
		}

	}
	
	@GetMapping("/delete-contact/{contactId}")
	public String deleteContact(
			Model model,
			@PathVariable("contactId") int contactId,
			HttpSession httpSession
	) {
		
		Contact contact = this.contactRepository.findById(contactId).get();
		
		if(user.getId() == contact.getUser().getId()) {
			
			if(contact.getImage()!=null && !contact.getImage().isEmpty()) {
				
				//to prevent the default contact icon/image from getting deleted
				if(!contact.getImage().matches("user.png")) {
					
					if (!ImageHelper.deleteImage(contact.getImage())) {
						httpSession.setAttribute("message", new Message("Oops! Something went wrong, unable to delete contact." , "alert-danger"));
						return "redirect:/user/view-contacts/0";
					}
					
				}
				
			}
			
			contact.setUser(null);
			this.contactRepository.delete(contact);
			
			httpSession.setAttribute("message", new Message("Contact deleted successfully." , "alert-success"));
		}
		else {
			httpSession.setAttribute("message", new Message("Sorry! You are not authorized to delete this particular contact." , "alert-danger"));
		}
		

		return "redirect:/user/view-contacts/0";
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
