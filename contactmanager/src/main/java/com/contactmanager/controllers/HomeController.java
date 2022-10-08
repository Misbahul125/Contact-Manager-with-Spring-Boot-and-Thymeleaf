package com.contactmanager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

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
		
		return "signup";
	}
	
	@RequestMapping("/login")
	public String login(Model model) {
		
		model.addAttribute("title" , "Login - Contact Manager");
		
		return "login";
	}
	
}
