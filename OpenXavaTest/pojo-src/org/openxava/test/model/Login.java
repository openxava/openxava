package org.openxava.test.model;

import org.openxava.annotations.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

public class Login {
	
	@Column(length=20)
	private String user;
	
	@Column(length=15) @Stereotype("PASSWORD")
	private String password;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
