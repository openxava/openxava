package org.openxava.model.transients; 

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * To use as generic transient class for dialogs.
 * 
 * @since 6.0
 * @author Javier Paniza
 */

public class WithRequiredName implements java.io.Serializable {    
		
	@Column(length=50)
	@Required(message="{simple_required}")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}