package com.smart.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.Dao.UserRepository;
import com.smart.entities.User;

public class UserDetailsImpl implements UserDetailsService {

	private UserRepository userRepository;
	
	
	    public UserDetailsImpl(UserRepository userRepository) {
	        this.userRepository = userRepository;
	    }
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //fetching user from database
        User user = userRepository.getUserByUserName(username);
        if(user==null) {
        	throw new UsernameNotFoundException("Could not found user !!");
        }
        		
		return new CustomUserDetails(user);
	}

}
