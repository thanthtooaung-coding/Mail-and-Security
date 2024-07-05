package com.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.test.model.Email;
import com.test.service.EmailService;

@Controller
@RequestMapping("/email")
public class EmailController {

	@Autowired
	private EmailService emailService;
	
	@GetMapping({"","/"})
	private String sendEmail(Model model) {
		model.addAttribute("testMail", new Email());
		return "email/email";
	}
	
	@PostMapping("/send")
	private String send(@ModelAttribute Email email) {
		System.out.println(email.getToEmail()+email.getSubject()+email.getBody());
		emailService.sendEmail(email.getToEmail(), email.getSubject(), email.getBody());		
		return "redirect:/email";
	}
}
