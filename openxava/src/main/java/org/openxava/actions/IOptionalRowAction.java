package org.openxava.actions;

import java.util.*;

/**
 * tmr redoc
 * An action that can be available for the user or hidden, depend on a programmatic condition.
 * 
 * @since 7.5
 * @author Javier Paniza
 */
public interface IOptionalRowAction extends IAction { 
	
	/**
	 * tmr redoc
	 * If true the action will be available for the user, otherwise it will be hidden. <br/>  
	 * 
	 * This method is executed before determine if the action has to be shown, so before execute().
	 * The action is configured completely (injecting all needed objects and properties) before calling
	 * isAvailable().
	 */
	boolean isApplicableForRow(Map key);

}
