package org.openxava.actions;

import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.hibernate.*;
import org.openxava.jpa.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * 
 * @author Javier Paniza
 */

abstract public class BaseAction implements IAction, IRequestAction, IModuleContextAction, IChangeModeAction {
	
	private static Log log = LogFactory.getLog(BaseAction.class); 
	
	private Messages errors;
	private Messages messages;
	private Environment environment;
	private transient HttpServletRequest request;
	private ModuleContext context; 
	private String nextMode;
	
	public Messages getErrors() {
		return errors;
	}

	public void setErrors(Messages errors) { 
		this.errors = errors;
	}
	
	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}
	
	protected void addErrors(Messages errors) {
		this.errors.add(errors);
	}
	
	protected void addMessages(Messages messages) {
		this.messages.add(messages);
	}
	
	/**
	 * If some id is a String between quotes (') it will be displayed literally, untranslated. 
	 */	
	protected void addError(String messageId, Object ... ids) {
		errors.add(Messages.Type.ERROR, messageId, ids); 
	}
	
	/**
	 * If some id is a String between quotes (') it will be displayed literally, untranslated. 
	 */	
	protected void addMessage(String messageId, Object ... ids) {
		messages.add(Messages.Type.MESSAGE, messageId, ids);  
	}
	
	/**
	 * If some id is a String between quotes (') it will be displayed literally, untranslated.
	 * 
	 * @since 4.3 
	 */	
	protected void addInfo(String messageId, Object ... ids) {
		messages.add(Messages.Type.INFO, messageId, ids);
	}
	
	/**
	 * If some id is a String between quotes (') it will be displayed literally, untranslated.
	 * 
	 * @since 4.3
	 */	
	protected void addWarning(String messageId, Object ... ids) {
		messages.add(Messages.Type.WARNING, messageId, ids);
	}
			
	public void executeBefore() throws Exception {	
	}
	
	public void executeAfter() throws Exception {	
	}
	
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	public Environment getEnvironment() {
		return environment;
	}
	
	/**
	 * Reset the cache of all descriptions-list and 
	 * others uses of descriptionsEditors.	 
	 */
	protected void resetDescriptionsCache() {
		DescriptionsLists.resetDescriptionsCache(request.getSession());		
	}
	
	public void setRequest(HttpServletRequest request) { 
		this.request= request;
	}
	
	/**
	 * With this method you can access directly to the
	 * web application resources, but it ties you to 
	 * implementation technology (servlets), hence it's 
	 * better to avoid it if you have alternative and 
	 * are thinking in migrating to another tecnology. 
	 * 
	 * @since 4m1
	 */
	protected HttpServletRequest getRequest() { 
		return this.request;
	}
	
	/**
	 * The Locale of the current request. <p> 
	 */
    protected Locale getLocale() {
        return Locales.getCurrent();
    }

	/**
	 * @since 4m1
	 */    
	protected ModuleContext getContext() {  
		if (context == null) throw new XavaException("context_null_in_action");
		return context;
	}

	public void setContext(ModuleContext context) {
		this.context = context;
	}

	/**
	 * @since 4m1
	 */
	public String getNextMode() {
		return nextMode;
	}

	/**
	 * @since 4m1
	 */	
	protected void setNextMode(String nextMode) {
		this.nextMode = nextMode;
	}

	/**
	 * @since 4m1
	 */	
	protected ModuleManager getManager() { 
		return (ModuleManager) getContext().get(request, "manager");
	}	
	
	/**
	 * Actions add added even if they are hidden. <p>
	 * 
	 * @since 4m2
	 */		
	protected void addActions(String ... qualifiedActions) { 
		for (String qualifiedAction: qualifiedActions) {
			if (Is.emptyString(qualifiedAction)) continue; 
			MetaAction action = MetaControllers.getMetaAction(qualifiedAction);
			if (action.isHidden()) { 
				action = action.cloneMetaAction();
				action.setHidden(false);
			}
			getManager().addMetaAction(action); 
		}
	}
	
	/**
	 * @since 4m2
	 */		
	protected void removeActions(String ... qualifiedActions) { 
		for (String qualifiedAction: qualifiedActions) {
			if (Is.emptyString(qualifiedAction)) continue;
			getManager().removeMetaAction(MetaControllers.getMetaAction(qualifiedAction));
		}
	}
	
	/**
	 * @since 4m2
	 */		
	protected void clearActions() {		
		getManager().memorizeControllers();										
		getManager().setControllersNames(IChangeControllersAction.EMPTY_CONTROLLER);
	}
	
	/**
	 * @since 4m2
	 */	
	protected void setControllers(String ... controllers) {  
		if (!(this instanceof IChangeControllersAction)) {
			getManager().setControllers(controllers); 
		}
		else {
			log.warn(XavaResources.getString("change_controllers_action_over_set_contoller", getClass())); 
		}
	}

	/**
	 * @since 4m2
	 */	
	protected void returnToPreviousControllers() { 
		setControllers(IChangeControllersAction.PREVIOUS_CONTROLLERS); 
	}
	
	/**
	 * Set the default controllers of the current module. <p>
	 * 
	 * The defaults controllers are those declared in application.xml for the
	 * module, so the initial controllers when the module starts. <br>
	 * Also, this method clear the stack of controllers navigation, that is, if after
	 * calling to this method you call to {@link returnToPreviousController}() it will 
	 * not have effect.<br>
	 * 
	 * @since 4m2
	 */
	protected void setDefaultControllers() { 
		setControllers(IChangeControllersAction.DEFAULT_CONTROLLERS); 
	}
	
	/**
	 * Commit the current transaction.
	 * 
	 * @since 5.0 
	 */
	protected void commit() {
		XPersistence.commit();
		XHibernate.commit();		
	}

	/**
	 * Rollback the current transaction.
	 * 
	 * @since 5.0
	 */	
	protected void rollback() { 		
		XPersistence.rollback();
		XHibernate.rollback();		
	}
	
	/**
	 * Execute an action, you can call it several times, for several actions.
	 * 
	 * It does not throw exceptions but accumulate the errors in getErrors(). Also it can modify getMessages().
	 * 
	 * @since 5.7
	 */
	protected void executeAction(String qualifiedActionName) { 
		getManager().executeAction(qualifiedActionName, getErrors(), getMessages(), getRequest()); 
	}
	
	
	/**
	 * From an action simple name get the qualified name of the action. <br>
	 * 
	 * If the action is not available returns null.<br>
	 * For example, from "new" gets "CRUD.new" if it exists, but if there is no
	 * an action called "new" in that moment it returns null.  
	 *  
	 * @since 5.8
	 */
	protected String getQualifiedActionIfAvailable(String simpleName) {   
		for (MetaAction action: getManager().getMetaActions()) {
			if (action.getName().equals(simpleName)) {
				return action.getQualifiedName();
			}
		}
		return null;
	}
	       
}
