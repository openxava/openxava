package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
public class Article {
	
	@Id
	private int number;
	
	@Column(length=40)
	private String description;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	

}
