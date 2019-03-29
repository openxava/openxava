package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;
import org.openxava.test.actions.*;

/**
 *
 * @author Javier Paniza
 */

@Entity
public class CheckTransaction extends Identifiable {
	
	@Column(length=40)
	private String description;
	
	@OneToOne(optional = true, cascade=CascadeType.ALL)
	@OnChange(OnChangeVoidAction.class) 
	private Check check;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Check getCheck() {
		return check;
	}

	public void setCheck(Check check) {
		this.check = check;
	}

}
