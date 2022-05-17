package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
public class Debt extends Identifiable {
	
	@Column(length=40) @Required
	private String description;
		
	private boolean paid;

	@Column(length=9) @Editor("Important")	
	private String important;
	
	@Depends("paid, important")
	public String getStatus() {
		return important + ": " + paid; 
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public String getImportant() {
		return important;
	}

	public void setImportant(String important) {
		this.important = important;
	}

}
