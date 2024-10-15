package com.smart.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "CONTACT")
public class Contacts {
	
	  @Id
	  @GeneratedValue(strategy = GenerationType.AUTO)
	  private int Cid;
      private String cname;
      private String Cemail;
      
      private String phone;
      
      @Column(length = 10000)
      private String decription;
      private String work;
      private String secondName;
      
      @Column(name = "Cimage")
      private String Cimage;
      
     @Override
	public String toString() {
		return "Contacts [Cid=" + Cid + ", Cname=" + cname + ", Cemail=" + Cemail + ", phone=" + phone + ", decription="
				+ decription + ", work=" + work + ", secondName=" + secondName + ", Cimage=" + Cimage +  "]";
	}
     
     
     @ManyToOne
     @JoinColumn(name = "user_id")
     @JsonIgnore
     private User user;

      
      
      
	public Contacts() {
		super();
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	

	public int getCid() {
		return Cid;
	}
	public void setCid(int cid) {
		Cid = cid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getCemail() {
		return Cemail;
	}
	public void setCemail(String cemail) {
		Cemail = cemail;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDecription() {
		return decription;
	}
	public void setDecription(String decription) {
		this.decription = decription;
	}
	
	public void setWork(String work) {
		this.work = work;
	}
	
	public String getWork() {
		return work;
	}
	
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getCimage() {
		return Cimage;
	}
	public void setCimage(String cimage) {
		Cimage = cimage;
	}
      
      
}
