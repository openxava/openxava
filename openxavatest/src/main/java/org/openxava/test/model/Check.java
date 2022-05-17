package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.model.*;

/**
 *
 * @author Javier Paniza
 */

@Entity
public class Check extends Identifiable {
	
	@Column(length=40)
	private String description;
	
	@OneToOne(mappedBy = "check", optional=true)
	private CheckTransaction transaction;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CheckTransaction getTransaction() {
		return transaction;
	}

	public void setTransaction(CheckTransaction transaction) {
		this.transaction = transaction;
	}

}
