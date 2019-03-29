package org.openxava.model.transients; 

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * To use as generic transient class for dialogs.
 * 
 * @since 5.6
 * @author Javier Paniza
 */

public class WithRequiredLongName implements java.io.Serializable {    
		
	@Column(length=100)
	@DisplaySize(200)
	@Required(message="{simple_required}")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}