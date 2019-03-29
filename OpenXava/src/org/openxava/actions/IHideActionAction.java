package org.openxava.actions;

/**
 * Allows to hide an action. <p>
 * 
 * You can just use the removeActions() and clearActions() methods of {@link BaseAction} 
 * instead of implementing this interface.
 * 
 * @author Javier Paniza
 */

public interface IHideActionAction {
	
	/**
	 * The action to hide. <p>
	 * 
	 * @return If <code>null</code> no action is hide.
	 */	
	String getActionToHide();

}
