package com.contactmanager.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactmanager.models.User;
import com.contactmanager.repos.UserRepository;
import com.contactmanager.utils.Constants;
import com.contactmanager.utils.Message;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;

	//view URLs
	
	@RequestMapping("/")
	public String home(Model model) {
		
		model.addAttribute("title" , "Home - Contact Manager");
		
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		
		model.addAttribute("title" , "About - Contact Manager");
		
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		
		model.addAttribute("title" , "Signup - Contact Manager");
		model.addAttribute("user", new User());
		
		return "signup";
	}
	
	@RequestMapping("/login")
	public String login(Model model) {
		
		model.addAttribute("title" , "Login - Contact Manager");
		
		return "login";
	}
	
	
	//processing URLs
	
	//@RequestMapping(value = "/signupAction" , method = RequestMethod.POST)
	@PostMapping("/signupAction")
	public String signupAction(
			@ModelAttribute("user") User user,
			@RequestParam(value = "agreement" , defaultValue = "false") boolean agreement,
			Model model,
			HttpSession httpSession
	) {
		
		try {
			
			if(!agreement) {
				System.out.println("Please accept our terms and conditions");
				throw new Exception("Please accept our terms and conditions");
			}
			
			if(user.getEmail().contains("@uem.edu.in")) {
				user.setRole(Constants.ADMIN_USER.toString());
			} else {
				user.setRole(Constants.NORMAL_USER.toString());
			}
			
			user.setActive(true);
			
			System.out.println(agreement);
			System.out.println(user);
			
			this.userRepository.save(user);
			
			model.addAttribute("user" , user);
			
			httpSession.setAttribute("message", new Message("Successfully Registered !!" , "alert-success"));
			
			return "signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			httpSession.setAttribute("message", new Message("Something went wrong !! "+e.getMessage() , "alert-danger"));
			
			return "signup";
		}
		
	}
	
}
