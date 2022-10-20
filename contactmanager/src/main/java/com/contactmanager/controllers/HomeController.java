package com.contactmanager.controllers;

import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactmanager.models.EmailData;
import com.contactmanager.models.User;
import com.contactmanager.repos.UserRepository;
import com.contactmanager.service.EmailService;
import com.contactmanager.utils.Constants;
import com.contactmanager.utils.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;

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
	
	@RequestMapping("/signUp")
	public String signup(Model model) {
		
		model.addAttribute("title" , "Signup - Contact Manager");
		model.addAttribute("user", new User());
		
		return "signup";
	}
	
	@RequestMapping("/signIn")
	public String login(Model model) {
		
		model.addAttribute("title" , "Login - Contact Manager");
		
		return "login";
	}
	
	@GetMapping("/verify-email")
	public String verifyEmail(Model model) {
		
		model.addAttribute("title", "Verify Email");
		
		return "/verify-email";
		
	}
	
	
	//processing URLs
	
	//@RequestMapping(value = "/signupAction" , method = RequestMethod.POST)
	@PostMapping("/signupAction")
	public String signupAction(
			@Valid @ModelAttribute("user") User user,BindingResult bindingResult,
			@RequestParam(value = "agreement" , defaultValue = "false") boolean agreement,
			Model model,
			HttpSession httpSession
	) {
		
		try {
			
			if(!agreement) {
				System.out.println("Please accept our terms and conditions");
				throw new Exception("Please accept our terms and conditions");
			}
			
			//server side validation of form
			if(bindingResult.hasErrors()) {
				System.out.println("ERROR" + bindingResult.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			
			if(user.getEmail().contains("@uem.edu.in")) {
				user.setRole(Constants.ROLE_ADMIN.toString());
			} else {
				user.setRole(Constants.ROLE_USER.toString());
			}
			
			user.setActive(true);
			
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
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
	
	@PostMapping("/loginAction")
	public String loginAction() {
		
		return "login";
	}
	
	@PostMapping("/sendOTP")
	public String sendOTP(
			@RequestParam("username") String email,
			HttpSession httpSession
	) {
		
		Random rnd = new Random();
        int number = rnd.nextInt(999999);
        String otp =  String.format("%06d", number);

        if(this.emailService.sendEmail(new EmailData(email , otp))) {
        	
        	httpSession.setAttribute("message", new Message("Email verified successfully !!" , "alert-success"));
        	httpSession.setAttribute("otp", otp);
        	return "verify-otp";
        }
        else {
        	httpSession.setAttribute("message", new Message("Unable to verify email !!" , "alert-danger"));
        	return "verify-email";
        }
	
	}
	
}
