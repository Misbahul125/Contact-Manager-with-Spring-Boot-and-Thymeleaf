package com.contactmanager.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contactmanager.models.User;
import com.contactmanager.repos.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/index")
	public String dashboard(Model model , Principal principal) {
		
		User user = userRepository.getUserByUserName(principal.getName());
		
		model.addAttribute("title", "Dashboard");
		model.addAttribute("user", user);
		
		return "normal/user_dashboard";
	}
	
}
