package com.test.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.test.dto.ReqRes;
import com.test.entity.OurUsers;
import com.test.service.AuthService;

@Controller
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@GetMapping("/")
	public String signUpPage(Model model) {
		OurUsers ourUser = new OurUsers();
		System.out.println("index");
		model.addAttribute("ourUser", ourUser);
		return "user/index";
	}

	@GetMapping("/signup")
	public String signup(ModelMap model) {
		model.addAttribute("signup", new ReqRes());
		return "signup";
	}
	
	@PostMapping("/signup")
	public String signUp(ReqRes signUpRequest, Model model) {
		signUpRequest.setRole("USER");
		ReqRes req = authService.signUp(signUpRequest);
		model.addAttribute("req", req);
		return "redirect:/login";
	}

//	@PostMapping("/login")
//	public String signIn(@ModelAttribute("login") ReqRes signInRequest, ModelMap model, Authentication authentication) {
//	    ReqRes response = authService.signIn(signInRequest);
//	    if (response.getRole().equals("ADMIN")) {
//	        model.addAttribute("user", authentication.getAuthorities());
//	        return "/user/index";
//	    } else {
//	        model.addAttribute("user", response);
//	        return "/user/index";
//	    }
//	}

	@PostMapping("/login")
	public String signIn(@ModelAttribute("login") ReqRes signInRequest, ModelMap model, Authentication authentication, RedirectAttributes redirectAttributes) {
	    ReqRes response = authService.signIn(signInRequest, authentication);
	    System.out.println("hiiiiiiiiiiiiiii");
	    authentication = SecurityContextHolder.getContext().getAuthentication();
	    //System.out.println("details="+authentication.getDetails());
	    System.out.println(authentication.getName());
	    System.out.println("details"+authentication.getDetails());
	    model.addAttribute("email", authentication.getName());
	    System.out.println("authen="+authentication.getAuthorities());
	    if (response.getRole().equals("ADMIN")) {
	        model.addAttribute("user", authentication.getAuthorities());
	        System.out.println("user"+authentication);
	        model.addAttribute("authen", authentication.getPrincipal());
	        System.out.println(authentication.getPrincipal());
	        System.out.println(authentication.getAuthorities());
	        return "admin/index";
	    } else {
	        model.addAttribute("user", authentication.getAuthorities());
	        System.out.println("user"+authentication);
	        System.out.println(authentication.getAuthorities());
	        redirectAttributes.addFlashAttribute("authen", authentication.getPrincipal()); // Pass the principal as a flash attribute
	        redirectAttributes.addFlashAttribute("userAuthorities", authentication.getAuthorities()); // Pass the user's authorities as a flash attribute
	        return "redirect:/auth/index";
	    }
	}

	
	@GetMapping("/index")
	public String userIndex(ModelMap model, Authentication authentication, @ModelAttribute("userAuthorities") Collection<? extends GrantedAuthority> userAuthorities, @ModelAttribute("authen") UserDetails userDetails) {
	    System.out.println("indexxxxxxxxxx");	    
	    authentication = SecurityContextHolder.getContext().getAuthentication();
	    
	    model.addAttribute("user", userAuthorities); // Add the user's authorities to the model
	    model.addAttribute("authen", userDetails); // Add the principal to the model
	    
	    System.out.println("principal" + userDetails);
	    return "user/index";
	}


	

	@GetMapping("/signin")
	public String signin(ModelMap model) {
		model.addAttribute("login", new ReqRes());
		return "login";
	}

	@PostMapping("/refresh")
	public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes refreshTokenRequest) {
		return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
	}
}
