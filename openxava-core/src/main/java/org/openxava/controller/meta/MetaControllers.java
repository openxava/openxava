package org.openxava.controller.meta;


import java.util.*;



import org.apache.commons.logging.*;
import org.openxava.controller.meta.xmlparse.*;
import org.openxava.util.*;

public class MetaControllers {
	
	private static Log log = LogFactory.getLog(MetaControllers.class);
		
	/** For context property */
	public final static String SWING="swing";
	/** For context property */
	public final static String WEB="web";
	
	private static Map environmentVariables;
	private static Map<String, MetaController> metaControllers;
	private static Map<String, MetaObject> mapMetaObjects;
	private static Collection<String> objectPrefixes; 
	private static String context = WEB;
	
	
	public static void _addMetaController(MetaController newController) throws XavaException {
		if (metaControllers == null) {
			throw new XavaException("only_from_parse", "MetaControllers._addMetaController");
		}
		metaControllers.put(newController.getName(), newController);
	}
	
	private synchronized static void setup() throws XavaException {
		if (metaControllers != null) return;
		metaControllers = new HashMap();
		ControllersParser.configureControllers(context);		
	}
	
	public synchronized static MetaController getMetaController(String name) throws ElementNotFoundException, XavaException {
		if (metaControllers == null) {
			setup();
		}
		MetaController result = (MetaController) metaControllers.get(name);
		if (result == null) {
			throw new ElementNotFoundException("controller_not_found", name);				
		}
		return result;
	}
	
	public static boolean contains(String name) throws XavaException {
		if (metaControllers == null) {
			setup();
		}
		return metaControllers.containsKey(name);
	}
	
	/** @since 6.1.2 */
	public static boolean containsMetaAction(String qualifiedName) { 
		try {
			getMetaAction(qualifiedName);
			return true;
		}
		catch (ElementNotFoundException ex) {
			return false;
		}
	}
	
	public static MetaAction getMetaAction(String qualifiedName) throws ElementNotFoundException, XavaException {
		if (metaControllers == null) {
			setup();
		}		
		if (qualifiedName == null) {
			throw new ElementNotFoundException("action_from_null_not_found");
		}
		if (qualifiedName.trim().equals("")) {
			throw new ElementNotFoundException("action_from_empty_string_not_found");
		}
		if (qualifiedName.indexOf('.') < 0) {
			throw new XavaException("only_qualified_action", qualifiedName);
		}
		StringTokenizer st = new StringTokenizer(qualifiedName, ".");
		String controller = st.nextToken().trim();
		String action = st.nextToken().trim();
		return getMetaController(controller).getMetaAction(action);
	}
	
	public static void addMetaObject(MetaObject object) {
		if (mapMetaObjects == null) mapMetaObjects = new HashMap();		
		mapMetaObjects.put(object.getName(), object);		
	}
	
	
	public static MetaObject getMetaObject(String name) throws ElementNotFoundException, XavaException {
		if (metaControllers == null) {
			setup();
		}						
		if (mapMetaObjects == null) throw new ElementNotFoundException("session_object_not_found_in_controllers", name);
		MetaObject a = (MetaObject) mapMetaObjects.get(name);
		if (a == null) throw new ElementNotFoundException("session_object_not_found_in_controllers", name);		
		return a; 
	}
	
	public static boolean containsMetaObject(String name) { 
		if (metaControllers == null) {
			setup();
		}						
		if (mapMetaObjects == null) return false;
		return mapMetaObjects.containsKey(name);
	}
	
	public static String getContext() {
		return context;
	}

	public static void setContext(String string) {
		context = string;
	}
	
	public static void addEnvironmentVariable(String name, String value) {
		if (environmentVariables == null) environmentVariables = new HashMap();
		environmentVariables.put(name, value);
	}
	
	/**	 	
	 * @return Null if it does not exist.	 
	 */
	public static String getEnvironmentVariable(String name) throws XavaException {
		if (metaControllers == null) {
			setup();
		}
		if (environmentVariables == null) return null;
		return (String) environmentVariables.get(name);						
	}

	public static Collection<String> getObjectPrefixes() {
		if (objectPrefixes == null) {
			if (metaControllers == null) { 
				setup();
			}
			objectPrefixes = new HashSet();
			for (MetaObject object: mapMetaObjects.values()) {
				int idx = object.getName().indexOf('_'); 
				if (idx >= 0) {
					objectPrefixes.add(object.getName().substring(0, idx)); 
				}
			}		
		}
		return objectPrefixes;
	}	
	
}


