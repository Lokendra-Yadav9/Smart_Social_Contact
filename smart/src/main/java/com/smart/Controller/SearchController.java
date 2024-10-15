package com.smart.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.Dao.ContactRepository;
import com.smart.Dao.UserRepository;
import com.smart.entities.Contacts;
import com.smart.entities.User;

@RestController
public class SearchController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String queryString, Principal principal) {
        
        // Log the search query for debugging
//        System.out.println(queryString);
        
        // Get the authenticated user's username
        String userName = principal.getName();
        
        // Fetch the user entity from the database using the username
        User user = userRepository.getUserByUserName(userName);
        
        // Handle the case where the user is not found
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        
        
        // Search for contacts by name containing the query string and belonging to the user
        List<Contacts> contactList = contactRepository.findByCnameContainingAndUser(queryString, user);
//        System.out.println(contactList);

//        System.out.println("User "+user);
        
        // Return the list of contacts as the response
        return ResponseEntity.ok(contactList);
    }
}
