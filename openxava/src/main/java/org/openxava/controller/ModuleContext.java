package org.openxava.controller;

import java.util.*;

import jakarta.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.web.servlets.*;

/**
 * Context with life of session and private for every module.
 * 
 * @author Javier Paniza
 */

public class ModuleContext implements java.io.Serializable { 
	
	private static Log log = LogFactory.getLog(ModuleContext.class);
	final private static ThreadLocal<String> currentWindowId = new ThreadLocal<String>(); 
	
	static {
		MetaControllers.setContext(MetaControllers.WEB);		
	}
	
	private Map<String, Map> contexts = null;
	private Map globalContext = null;
	private String lastUsedWindowId; 
	private String windowIdForNextTime = null;

	/**
	 * Return a object associated to the specified module
	 * in 'application' and 'module' of request.
	 * @throws XavaException
	 */
	public Object get(HttpServletRequest request, String objectName) {  
		String application = request.getParameter("application");
		application = Is.emptyString(application) ? (String) request.getAttribute("xava.application") : application; 
		if (Is.emptyString(application)) throw new XavaException("application_and_module_required_in_request");

		String module = request.getParameter("module");
		module = Is.emptyString(module) ? (String) request.getAttribute("xava.module") : module;
		if (Is.emptyString(module)) throw new XavaException("application_and_module_required_in_request");
		
		return get(application, module, objectName);		
	}
	
	/**
	 * Return a object asociate to the specified module
	 * in 'application' and 'module' of request.
	 * @throws XavaException
	 */
	public Object get(HttpServletRequest request, String objectName, String className) { 
		String application = request.getParameter("application");
		if (Is.emptyString(application)) {
			throw new XavaException("application_and_module_required_in_request");
		}
		String module = request.getParameter("module");
		if (Is.emptyString(module)) {
			throw new XavaException("application_and_module_required_in_request");
		}		
		return get(application, module, objectName, className);		
	}
	
	
	/**
	* @throws XavaException
	 */
	public Object get(String application, String module, String objectName, String className) {
		Map context = getContext(application, module, objectName); 
		Object o = context.get(objectName);
		if (o == null) {
			o = createObjectFromClass(className);
			context.put(objectName, o);			
		}
		return o;
	}
	
	

	/**
	* @throws XavaException
	 */
	private Object createObjectFromClass(String className) {
		try {
			return Class.forName(className).newInstance();
		}
		catch (Exception ex) {
			log.error(ex.getMessage(),ex);
			throw new XavaException("create_error", className);
		}
	}

	/**
	 * If does not exist the it create one, as defined in controllers.xml. <p>
	 * @throws XavaException
	 */	
	public Object get(String application, String module, String objectName) {
		Map context = getContext(application, module, objectName);
		Object o = context.get(objectName);
		if (o == null) {
			o = createObject(objectName);
			context.put(objectName, o);			
		}
		return o;
	}
	
	/**
	* @throws XavaException
	 */
	public boolean exists(String application, String module, String objectName) {
		Map context = getContext(application, module, objectName); 
		return context.containsKey(objectName);
	}
	
	/**
	* @throws XavaException
	 */
	public boolean exists(HttpServletRequest request, String objectName) {
		String application = request.getParameter("application");
		if (Is.emptyString(application)) {
			throw new XavaException("application_and_module_required_in_request");
		}
		String module = request.getParameter("module");
		if (Is.emptyString(module)) {
			throw new XavaException("application_and_module_required_in_request");
		}		
		return exists(application, module, objectName);		
	}	


	/**
	* @throws XavaException
	 */
	public void put(HttpServletRequest request, String objectName, Object value) {
		Map context = getContext(request, objectName); 
		context.put(objectName, value);
	}
		
	/**
	* @throws XavaException
	 */
	public void put(String application, String module, String objectName, Object value) {
		Map context = getContext(application, module, objectName);
		context.put(objectName, value);
	}

	/**
	* @throws XavaException
	 */
	public void remove(HttpServletRequest request, String objectName) {
		Map context = getContext(request, objectName);
		context.remove(objectName);		
	}
		
	/**
	* @throws XavaException
	 */
	public void remove(String application, String module, String objectName) {
		Map context = getContext(application, module, objectName); 
		context.remove(objectName);
	}
	
	/**
	 * 
	 * @since 4.1.2
	 */
	public String getCurrentModule(HttpServletRequest request) {
		String module;
		String currentModule = request.getParameter("module");	
		do {
			module = currentModule;
			currentModule = (String) get(request.getParameter("application"), currentModule, "xava_currentModule");
		}
		while (!Is.empty(currentModule));
		return module;
	}
			
	/**
	* @throws XavaException
	 */
	private Object createObject(String objectName) {			
		return MetaControllers.getMetaObject(objectName).createObject();
	}
	
	/**
	 * Reset all the context state for the module. Actually reinit the module. <p>
	 * 
	 * @since 6.0
	 * @throws XavaException
	 */
	public void resetModule(HttpServletRequest request) {
		getContext(request, null).clear(); 
	}

	/**
	 * Reset all the context state for all the module but the current one. Actually reinit the modules. <p>
	 * 
	 * @since 6.0.2
	 * @throws XavaException
	 */	
	public void resetAllModulesExceptCurrent(HttpServletRequest request) { 
		Map current = getContext(request, null);
		for (Map context: getContexts().values()) {
			if (context != current) { 
				context.clear();
			}
		}
	}

	
	/**
	* @throws XavaException
	 */
	public Map getContext(HttpServletRequest request, String objectName) {  
		String application = request.getParameter("application");
		if (Is.emptyString(application)) {
			throw new XavaException("application_and_module_required_in_request");
		}
		String module = request.getParameter("module");
		if (Is.emptyString(module)) {
			throw new XavaException("application_and_module_required_in_request");
		}				
		return getContext(application, module, objectName); 
	}

	/**
	* @throws XavaException
	 */
	private Map getContext(String application, String module, String objectName) {
		if (isGlobal(objectName)) {
			return getGlobalContext();
		}

		if (currentWindowId.get() == null) currentWindowId.set(lastUsedWindowId);
		
		String id = application + "/" + module + "/" + currentWindowId.get();
		
		Map context = (Map) getContexts().get(id);
		if (context == null) {
			context = new HashMap();			
			getContexts().put(id, context);
		}
		return context;
	}
	
	/**
	* @throws XavaException
	 */
	private boolean isGlobal(String objectName) {
		try {
			return MetaControllers.getMetaObject(objectName).isGlobal();
		}
		catch (ElementNotFoundException ex) { 
			return false;
		}
	}

	/**
	 * Used for application scope objects.
	 */
	private Map getGlobalContext() {
		if (globalContext == null) {
			globalContext = new HashMap();
		}
		return globalContext;
	}
	
	private Map<String, Map> getContexts() { 
		if (contexts == null) {
			contexts = new HashMap();
		}
		return contexts;
	}

	
	/**
	 * All objects with this name in all the active modules of the user session.
	 */	
	public Collection getAll(String objectName) { 
		Collection allContexts = new ArrayList();
		if (contexts == null || contexts.isEmpty()) return allContexts;		
		
		Iterator it = contexts.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry context = (Map.Entry) it.next();
			Object object = ((Map)context.getValue()).get(objectName);
			if (object != null) allContexts.add(object);
		}
		return allContexts;
	}
		
	public String getWindowId(HttpServletRequest request) {
		// If we change this method we should try with Customer module: 
		//		New, change image, open Customer module in other browser tab, it should show the list, not the detail
		// That is the state of browser tabs is independent after we upload an image. 
		// This case cannot be tested with HtmlUnit, in some detail it does not behave as a real browser for this case.
		
		String alreadyInPageWindowId = (String) request.getAttribute("xava.new.window.id");
		if (alreadyInPageWindowId != null) {
			return alreadyInPageWindowId;
		}
		String windowId = Servlets.getCookie(request, "XAVA_WINDOW_ID");
		if (Is.emptyString(windowId)) {
			if (windowIdForNextTime != null) {								
				windowId = windowIdForNextTime;
			}
			else {
				windowId = Long.toHexString(System.currentTimeMillis()); // Better than UUID because is shorter and impossible to duplicate because a user cannot open two tabs at once in the same browser
			}
			request.setAttribute("xava.new.window.id", windowId);	
		}
		windowIdForNextTime = null;
		return windowId;
	}
	
	public void setCurrentWindowId(HttpServletRequest request) {
		String windowId = request.getHeader("xavawindowid");
		if (Is.emptyString(windowId) || "null".equals(windowId)) {
			windowId = request.getParameter("windowId");
		}
		// Fixes occasional loss of window id
		if (Is.emptyString(windowId) || "null".equals(windowId)) {
			windowId = getWindowId(request);
		}
		setCurrentWindowId(windowId);
	}
	
	public void setCurrentWindowId(String id) {
		String windowId = "null".equals(id)?null:id;
		currentWindowId.set(windowId);
		if (windowId != null) lastUsedWindowId = windowId;
	}
	
	public static void cleanCurrentWindowId() {
		currentWindowId.remove(); 
	}

	
	public void dontGenerateNewWindowIdNextTime() { 
		windowIdForNextTime = lastUsedWindowId;   
	}

}
