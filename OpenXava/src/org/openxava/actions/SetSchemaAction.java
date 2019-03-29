package org.openxava.actions;

import javax.inject.Inject;

import org.openxava.hibernate.XHibernate;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Is;

/**
 * 
 * @author Federico Alcantara
 */
public class SetSchemaAction extends ViewBaseAction {
	@Inject
	private String defaultSchema;
	
	private String newDefaultSchema;		

	public void execute() throws Exception {
		newDefaultSchema = getRequest().getParameter("schema");
		if (newDefaultSchema != null) {
			if (defaultSchema != null && !newDefaultSchema.equals(defaultSchema)
					&& getView() != null) {
				getView().clear();
			}
			defaultSchema = newDefaultSchema;	
			XPersistence.setDefaultSchema(
					Is.emptyString(defaultSchema) ? XPersistence.getDefaultSchema() : defaultSchema);		
			XHibernate.setDefaultSchema(
					Is.emptyString(defaultSchema) ? XHibernate.getDefaultSchema() : defaultSchema);
		}
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
