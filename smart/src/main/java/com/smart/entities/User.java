package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "USER")
public class User {
	
	  @Id
	  @GeneratedValue(strategy = GenerationType.AUTO)
      private int id;
	  
	  @NotBlank(message = "Name field is required to fill")
	  @Size(min = 2 , max = 20 , message = "min 2 or max 20 characters are allow")
      private String name ;
      
      @Column(unique = true)
      private String email;
      private String password;
      
      @Column(length = 500)
      private String about;
      
      @Column(nullable = false)
      private boolean isEnable;
      private String role;
      private String image_Url;
      
      
      @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY , mappedBy = "user" , orphanRemoval = true)
      List<Contacts> contact=new ArrayList<>();
      
      
      
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public List<Contacts> getContact() {
		return contact;
	}
	public void setContact(List<Contacts> contact) {
		this.contact = contact;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public boolean isEnable() {
		return isEnable;
	}
	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getImage_Url() {
		return image_Url;
	}
	public void setImage_Url(String image_Url) {
		this.image_Url = image_Url;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", about=" + about
				+ ", isEnable=" + isEnable + ", role=" + role + ", image_Url=" + image_Url + ", contact=" + contact
				+ "]";
	}
      
}
