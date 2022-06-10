package org.openxava.actions;

/**
 * Allows to show an action previously hidden. <p>
 * 
 * You can just use the addActions() method of {@link BaseAction} instead of 
 * implementing this interface.
 * 
 * @author Javier Paniza
 */

public interface IShowActionAction {

	/**
	 * The action to show. <p>
	 * 
	 * @return If <code>null</code> no action is shown.
	 */
	String getActionToShow();
	
}
