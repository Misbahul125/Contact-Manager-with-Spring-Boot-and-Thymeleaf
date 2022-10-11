package com.contactmanager.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Contact")
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int contactId;
	
	private String firstName;
	
	private String secondName;
	
	private String email;
	
	private String work;
	
	private String phone;
	
	private String image;
	
	@Column(length = 1000)
	private String description;
	
	@ManyToOne
	private User user;

	public Contact() {
		super();
	}

	public Contact(int contactId, String firstName, String secondName, String email, String work, String phone,
			String image, String description, User user) {
		super();
		this.contactId = contactId;
		this.firstName = firstName;
		this.secondName = secondName;
		this.email = email;
		this.work = work;
		this.phone = phone;
		this.image = image;
		this.description = description;
		this.user = user;
	}

	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Contact [contactId=" + contactId + ", firstName=" + firstName + ", secondName=" + secondName
				+ ", email=" + email + ", work=" + work + ", phone=" + phone + ", image=" + image + ", description="
				+ description + ", user=" + user + "]";
	}
	
	

}
