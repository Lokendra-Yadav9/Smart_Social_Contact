package com.smart.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.smart.Dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userrepository;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home-Main page");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About-info page");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Sign-up");
		model.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/do_action")
	public String register(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "Agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {

			if (!agreement) {
				System.out.println("you have not accept terms and conditions");
				model.addAttribute("user", new User());
				throw new Exception("you have not accept terms and conditions");
			}

			if (result.hasErrors()) {
				model.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnable(true);
			user.setImage_Url("DefaultImg.jpg");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			

			User savedUser = this.userrepository.save(user);

			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
			return "redirect:/signup";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
			return "redirect:/signup";
		}
	}

	@GetMapping("/signin")
	public String customLogin(Model model) {
		
		model.addAttribute("title","login page"); 
		return "login";
	}
}
