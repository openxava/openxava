package org.openxava.actions;

import org.openxava.view.*;

/**
 * This action is associated to an property (displayed in User Interface),
 * before execute it the property name and the container view is injected. <p>
 * 
 * This interface can be implemented by property defined in this way:
 * <pre>
 * <property-view property="street" label-format="NORMAL">
 * 		<action action="Customers.prefixStreet"/> 
 * </property-view>
 * </pre>
 * 
 * @author Javier Paniza
 */

public interface IPropertyAction extends IAction {
	
	/**
	 * The name of the property associated to this action,
	 */
	void setProperty(String propertyName);
	
	/**
	 * The container view of the property associated to this action. <p>
	 * 
	 * It's not the main view, but the more inner view when the action resides.<br>
	 */
	void setView(View view);
	
}
