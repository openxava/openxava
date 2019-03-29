package org.openxava.actions;


import javax.inject.*;
import org.openxava.jpa.*;

/**
 * 
 * @since 4.2.2
 * @author Javier Paniza
 */

public class SetPersistenceUnitAction extends BaseAction { 
	
	@Inject
	private String persistenceUnit;
	private String newPersistenceUnit;		

	public void execute() throws Exception {			
		if (newPersistenceUnit != null)	 persistenceUnit = newPersistenceUnit;	
		XPersistence.setPersistenceUnit(persistenceUnit); 
	}

	public String getPersistenceUnit() {
		return persistenceUnit;
	}

	public void setPersistenceUnit(String persistenceUnit) {
		this.persistenceUnit = persistenceUnit;
	}

	public String getNewPersistenceUnit() {
		return newPersistenceUnit;
	}

	public void setNewPersistenceUnit(String newPersistenceUnit) {
		this.newPersistenceUnit = newPersistenceUnit;
	}

}
