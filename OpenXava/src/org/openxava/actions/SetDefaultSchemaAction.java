package org.openxava.actions;

import javax.inject.*;

import org.openxava.hibernate.*;
import org.openxava.jpa.*;

/**
 * Set value of default schema for JPA Persistence and
 * updates the session object 'defaultSchema'. <p> 
 * 
 * Useful for setting the default schema to be used by
 * JPA and Hibernate, the change only apply to the current thread, 
 * and all the changes are reset just before each request.<br>
 * 
 * @author Javier Paniza
 */

public class SetDefaultSchemaAction extends BaseAction {
	
	@Inject
	private String defaultSchema;
	private String newDefaultSchema;		

	public void execute() throws Exception {			
		if (newDefaultSchema != null)	defaultSchema = newDefaultSchema;	
		XPersistence.setDefaultSchema(defaultSchema);		
		XHibernate.setDefaultSchema(defaultSchema);
	}

	/**
	 * The current default schema used by OpenXava and JPA.
	 */
	public String getDefaultSchema() {
		return defaultSchema;
	}

	/**
	 * The current default schema used by OpenXava and JPA.
	 */	
	public void setDefaultSchema(String company) {
		this.defaultSchema = company;
	}

	/**
	 * The new default schema for OpenXava and JPA. <P>
	 * 
	 * This value update the property 'defaultSchema'.
	 */
	public String getNewDefaultSchema() {
		return newDefaultSchema;
	}

	/**
	 * The new default schema for OpenXava and JPA. <P>
	 * 
	 * This value update the property 'defaultSchema'.
	 */	
	public void setNewDefaultSchema(String newCompany) {
		this.newDefaultSchema = newCompany;
	}

}
