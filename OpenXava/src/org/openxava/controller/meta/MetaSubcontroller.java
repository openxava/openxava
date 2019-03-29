package org.openxava.controller.meta;

import java.util.*;

/**
 * Create on 25/04/2013 (08:30:35)
 * @author Ana Andres
 */
public class MetaSubcontroller extends MetaControllerElement{
	
	private String controllerName;

	public boolean hasActionsInThisMode(String mode){
		Collection<MetaAction> actions = getMetaActions();
		for (MetaAction action : actions){
			if (action.appliesToMode(mode)) return true;
		}
		return false;
	}
	
	/**
	 * @since 5.4.1
	 */
	public MetaController getMetaController() {  
		return MetaControllers.getMetaController(getControllerName());
	}
	
	public Collection<MetaAction> getMetaActions(){
		return getMetaController().getMetaActions(); 
	}
	
	public String getControllerName() {
		return controllerName;
	}

	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}

	@Override
	public String getId() {
		return getControllerName();
	}

}
