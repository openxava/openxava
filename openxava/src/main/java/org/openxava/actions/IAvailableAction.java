package org.openxava.actions;

/**
 * An action that can be available for the user or hidden, depend on a programmatic condition.
 * 
 * tmr ¿Redoc para indicar que se puede usar en acciones de fila?
 * tmr Falta probar con 7.4.5 y 7.3.x
 * tmr Documentar con ejemplo en referencia
 * 
 * @since 5.9
 * @author Javier Paniza
 */
public interface IAvailableAction extends IAction { 
	
	/**
	 * If true the action will be available for the user, otherwise it will be hidden. <br/>  
	 * 
	 * This method is executed before determine if the action has to be shown, so before execute().
	 * The action is configured completely (injecting all needed objects and properties) before calling
	 * isAvailable().
	 */
	boolean isAvailable();

}
