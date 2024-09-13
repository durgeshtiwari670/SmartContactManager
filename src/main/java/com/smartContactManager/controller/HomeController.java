package com.smartContactManager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartContactManager.entities.User;
import com.smartContactManager.message.Message;
import com.smartContactManager.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@RequestMapping("/")
	public String home(Model model) {

		model.addAttribute("title", "Home - Smart Contact Manager");

		return "home";
	}

	@RequestMapping("/about")
	public String about(Model model) {

		model.addAttribute("title", "About - Smart Contact Manager");

		return "about";
	}

	@RequestMapping("/signup")
	public String signUp(Model model) {

		model.addAttribute("title", "Sign up - Smart Contact Manager");
		model.addAttribute("user", new User());

		return "signup";
	}

	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,

			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
			Model model, HttpSession session) {

		try {
			if (!agreement) {
				throw new Exception("User has not agreed the agreement");
			}

			if (result1.hasErrors()) {

				System.out.println("ERROR " + result1.toString());
				model.addAttribute("user", user);
				return "signup";
			}

			User result = this.userService.addUser(user);
			model.addAttribute("user", new User());
			System.out.println("agreement " + agreement);
			System.out.println("User " + result);

			session.setAttribute("message", new Message("Registered Successfully !!", "alert-success"));
			return "signup";

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
		}

		return "signup";
	}

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public String login(Model model) {
		model.addAttribute("title", "Login page");
		return "signin";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(Model model) {
		model.addAttribute("title", "Login page");
		return "signin";
	}
}
