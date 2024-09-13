package com.smartContactManager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartContactManager.dao.ContactRepository;
// import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.smartContactManager.dao.UserRepository;
import com.smartContactManager.entities.Contact;
import com.smartContactManager.entities.User;
import com.smartContactManager.message.Message;

import jakarta.servlet.http.HttpSession;

// import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		try {
			String userName = principal.getName();
			System.out.println("Username " + userName);

			User user = userRepository.getUserByUserName(userName);
			// System.out.println(user);
			model.addAttribute("user", user);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");

		return "normal/user_dashboard";
	}

	@RequestMapping(value = "/add-contact", method = RequestMethod.GET)
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";

	}

	// processing add contact form

	@RequestMapping(value = "/process-contact", method = RequestMethod.POST)
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			if (file.isEmpty()) {
				System.out.println("file is empty");
				contact.setImage("default-contact.png");
			} else {
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded successfully");
			}
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			System.out.println("contact added in the current user contact list");

			session.setAttribute("massage", new Message("Your contact is added !!..", "success"));
		} catch (Exception e) {
			System.out.println("error in" + e.getMessage());
			e.printStackTrace();
			session.setAttribute("massage", new Message("Something went wrong !!..", "danger"));
		}
		// System.out.println("contact" + contact);
		return "normal/add_contact_form";
	}

	@GetMapping("/show-contact/{page}")
	public String showContact(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "show User Contacts");

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		Pageable pageable = PageRequest.of(page, 5, Sort.unsorted());
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPage", contacts.getTotalPages());
		return "normal/show_contacts";
	}

	// single contact detail controller

	@GetMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model) {

		Optional<Contact> optionalContact = this.contactRepository.findById(cId);
		Contact contact = optionalContact.get();
		model.addAttribute("title", "Show Contact Details");
		model.addAttribute("contact", contact);
		return "normal/contact_detail";
	}
}
