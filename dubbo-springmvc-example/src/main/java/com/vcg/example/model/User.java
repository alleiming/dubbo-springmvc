package com.vcg.example.model;

import java.util.Date;

public class User {

	private Integer id;

	private String username;

	private String password;

	private Date registerDate;

	public User(Integer id, String username, String password, Date registerDate) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.registerDate = registerDate;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", registerDate=" + registerDate
				+ "]";
	}
	
	

}
