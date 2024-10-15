package com.smart.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Dao.ContactRepository;
import com.smart.Dao.UserRepository;
import com.smart.entities.Contacts;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.helper.UserSortName;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// send the data to new page
	@ModelAttribute
	public void AddCommmonData(Model model, Principal principal) {
		String Username = principal.getName();

		User user = userRepository.getUserByUserName(Username);

		model.addAttribute("user",user);

		UserSortName userSortName = new UserSortName();

		// Extract and add user initials to the model
		String userInitials = userSortName.getInitials(user.getName());
		model.addAttribute("userInitials", userInitials);
	}

	// this is a dash-board page
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "user_Dashboard");

		// Call AddCommonData to add user data to the model
		return "normal/user_dashboard";
	}

	// this is for add new contact
	@GetMapping("/new-contact")
	public String addContact(Model model, Principal principal) {
		model.addAttribute("title", "Add new Contact");
		model.addAttribute("contact", new Contacts());
		return "normal/add_new_contact";
	}

	// this is the add new contact
	@PostMapping("/process-contact")
	public String processContact(@RequestParam("Cimage") MultipartFile file, @RequestParam("Cname") String Cname,
			@RequestParam("secondName") String secondName, @RequestParam("phone") String phone,
			@RequestParam("Cemail") String Cemail, @RequestParam("work") String work,
			@RequestParam("decription") String decription, Principal principal, Model model, HttpSession session) {

		String UName = principal.getName();
		User user = userRepository.getUserByUserName(UName);

		try {
			Contacts contact = new Contacts();
			contact.setCname(Cname);
			contact.setSecondName(secondName);
			contact.setPhone(phone);
			contact.setCemail(Cemail);
			contact.setWork(work);
			contact.setDecription(decription);

			if (file.isEmpty()) {
				contact.setCimage("default.png");

			} else {
				String originalFileName = file.getOriginalFilename();
				String newFileName = user.getId() + "_" + System.currentTimeMillis() + "_" + originalFileName;
				contact.setCimage(newFileName);

				File saveFile = new File("src/main/resources/static/img/");
				if (!saveFile.exists()) {
					saveFile.mkdirs();
				}

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + newFileName);
				Files.copy(file.getInputStream(), path);
				System.out.println("Image uploaded successfully");
			}

			contact.setUser(user);
			user.getContact().add(contact);
			userRepository.save(user);
			session.setAttribute("message", new Message("Contact saved Successfully", "alert-success"));

		} catch (Exception e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
		}

		return "normal/add_new_contact";
	}

	@GetMapping("/view-contact/{page}")
	public String showContact(@PathVariable("page") Integer page,
			@RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize, Model model, Principal principal) {
		model.addAttribute("title", "View Contact");

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		Integer userId = user.getId();

		// Ensure pageSize is valid (greater than 0)
		if (pageSize <= 0) {
			pageSize = 10; // Default pageSize
		}

		Pageable pageable = PageRequest.of(page, pageSize);
		Page<Contacts> Clist = this.contactRepository.getContactByUsername(user.getId(), pageable);

		model.addAttribute("Contacts", Clist);
		model.addAttribute("UserId", userId);
		model.addAttribute("hasContacts", Clist.getTotalElements() > 0);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", Clist.getTotalPages());
		model.addAttribute("pageSize", pageSize); // Ensure pageSize is set in the model

		return "normal/view-contact";
	}

	@RequestMapping("/{Cid}/contact")
	public String Show_contact_details(@PathVariable("Cid") Integer Cid,
			@RequestParam(name = "page", defaultValue = "0") Integer page,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, Model model, Principal principal) {
		System.out.println("cid " + Cid);
		Optional<Contacts> contactoptional = this.contactRepository.findById(Cid);
		Contacts contacts = contactoptional.get();

		String userName = principal.getName();

		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contacts.getUser().getId()) {
			model.addAttribute("Contacts", contacts);
			model.addAttribute("currentPage", page);
			System.out.println(page);
			System.out.println(pageSize);
			model.addAttribute("pageSize", pageSize);
			model.addAttribute("title", contacts.getCname());
		}

		return "normal/contact_details";
	}

	// Controller method for the delete logic
	@GetMapping("/delete/{Cid}")
	public String deleteContact(@PathVariable("Cid") Integer Cid, @RequestParam("page") int page,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize, Model model, Principal principal,
			HttpSession session) {
		// Getting contact
		Optional<Contacts> contacts = this.contactRepository.findById(Cid);

		if (contacts.isPresent()) {
			Contacts contact = contacts.get();

			// Getting the logged-in user
			String userName = principal.getName();
			User user = this.userRepository.getUserByUserName(userName);

			// Check if the contact belongs to the logged-in user
			if (user.getId() == contact.getUser().getId()) {

				String ImageFileName = contact.getCimage();
				if (!"default.jpg".equals(ImageFileName)) {
					Path myImgpath = Paths.get("src/main/resources/static/img/" + ImageFileName);
					try {
						Files.deleteIfExists(myImgpath);
//						System.out.println(myImgpath);
//						 System.out.println("image deleted");
					} catch (Exception e) {
						e.printStackTrace();
//					    System.out.println("image not deleted");
					}
				} else {
					System.out.println("default image deleted");
				}

				this.contactRepository.delete(contact);
				session.setAttribute("message", new Message("Contact deleted successfully", "alert-success"));
			} else {
				session.setAttribute("message",
						new Message("You do not have permission to delete this contact", "alert-danger"));
			}
		} else {
			session.setAttribute("message", new Message("Contact not found", "alert-danger"));
		}

		// Redirecting to the contact list page
		return "redirect:/user/view-contact/" + page + "?pageSize=" + pageSize;

		// http://localhost:8080/user/view-contact/1?pageSize=10&continue
	}

	@GetMapping("/delete-all/{userId}/confirm")
	public String showConfirmationPage(@PathVariable("userId") Integer userId, Model model) {
		Optional<User> userOptional = userRepository.findById(userId);

		if (userOptional.isPresent()) {
			model.addAttribute("userId", userId);
			model.addAttribute("userName", userOptional.get().getName()); // Replace with actual method to get user name
			return "confirmation"; // Name of the Thymeleaf template
		} else {
			model.addAttribute("message", "User not found");
			return "redirect:/user/view-contact/0";
		}
	}

	@Transactional
	@GetMapping("/delete-all/{userId}")
	public String deleteAll(@PathVariable("userId") Integer userId, Model model) {
		Optional<User> userOptional = userRepository.findById(userId);

		if (userOptional.isPresent()) {

			// Retrieve all contacts associated with the user
			List<Contacts> contactsList = contactRepository.findByUserId(userId);

			// Iterate over contacts to delete images
			for (Contacts contact : contactsList) {
				String imageFileName = contact.getCimage();
				if (!"default.jpg".equals(imageFileName)) {
					Path imagePath = Paths.get("src/main/resources/static/img/" + imageFileName);
					try {
						Files.deleteIfExists(imagePath);
					} catch (Exception e) {
						e.printStackTrace(); // Handle exception if image deletion fails
					}
				}
			}

			// Delete all contacts associated with the user
			model.addAttribute("message", "Contacts deleted successfully");
			contactRepository.deleteByUserId(userId);
			// Optional: Add a success message or any other feedback

		} else {
			// Optional: Add an error message or any other feedback
			model.addAttribute("message", "User not found");
		}

		return "redirect:/user/view-contact/0";
	}

	// Open Contact for Editing
	@PostMapping("/update-Contact/{cid}")
	public String editContact(@PathVariable("cid") Integer cid, Model model, HttpSession session) {
		try {
			model.addAttribute("title", "update Contact");
			// Fetch the existing contact from the repository
			Contacts contact = contactRepository.findById(cid).get();

			// Add the contact details to the model
			model.addAttribute("contact", contact);

			return "normal/update-Contact"; // The view name to display the contact edit form

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
			return "redirect:/user/view-contacts"; // Redirect to a contacts list or error page
		}
	}

	// Update method with MultipartFile for image handling
	@PostMapping("/process-update")
	public String processUpdate(@ModelAttribute("contact") Contacts contact, @RequestParam("file") MultipartFile file,
			HttpSession session, Principal principal) {
		try {
			Contacts existingContact = contactRepository.findById(contact.getCid())
					.orElseThrow(() -> new RuntimeException("Contact not found"));

			existingContact.setCname(contact.getCname());
			existingContact.setSecondName(contact.getSecondName());
			existingContact.setPhone(contact.getPhone());
			existingContact.setCemail(contact.getCemail());
			existingContact.setWork(contact.getWork());
			existingContact.setDecription(contact.getDecription());

			if (!file.isEmpty()) {
				String fileName = saveImage(file, existingContact);
				existingContact.setCimage(fileName);
			}

			contactRepository.saveAndFlush(existingContact);
			session.setAttribute("message", new Message("Contact updated successfully", "alert-success"));
			return "redirect:/user/" + existingContact.getCid() + "/contact";

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
			return "redirect:/user/update-Contact";
		}

	}

	public static String saveImage(MultipartFile file, Contacts contact) throws IOException {
		if (file.isEmpty()) {
			throw new IOException("Failed to save empty file.");
		}

		// Define the directory to save the uploaded image
		String uploadDir = "src/main/resources/static/img/";
		// Generate a new file name using the contact ID and current timestamp
		String fileName = contact.getCid() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

		// Create the directory if it does not exist
		File uploadDirFile = new File(uploadDir);
		if (!uploadDirFile.exists()) {
			uploadDirFile.mkdirs();
		}

		// Save the file to the server
		Path path = Paths.get(uploadDir, fileName);
		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

		return fileName;
	}
	
	
	@GetMapping("/your-profile")
	public String yourProfile(Model model,Principal principal,User user) {
		model.addAttribute("title","Profile");
		UserSortName userSortName=new UserSortName();
		String[] names= userSortName.extractNameParts(user.getName());
		String roles= userSortName.extractROleParts(user.getRole());
        model.addAttribute("names",names);
        model.addAttribute("roles",roles);
    
		return "normal/User_Profile";
	}
	
	
	@GetMapping("/your-setting")
	public String yourSetting(Model model, Principal principal) {
		
		String Username = principal.getName();
		User user = userRepository.getUserByUserName(Username);
		model.addAttribute("user",user);
		
		model.addAttribute("title","Your Setting");
		return "normal/setting";
	}
	
	
	
	

}
