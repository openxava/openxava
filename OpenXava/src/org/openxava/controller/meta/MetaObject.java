package org.openxava.controller.meta;

import java.lang.reflect.*;
import java.util.*;



import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class MetaObject {
	
	private static Log log = LogFactory.getLog(MetaObject.class);
	private final static Class [] PARAMETER_TYPES = { java.lang.String.class }; 
	
	private String name;
	private String className;
	private String value;
	private boolean global = false;
	
	
	public String getClassName() {
		return className;
	}

	public String getName() {
		return name;
	}

	public void setClassName(String string) {
		className = string;
	}

	public void setName(String string) {
		name = string;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String string) {
		value = string;
	}
	
	public Object createObject() throws XavaException {
		try {		
			Class clase = Class.forName(this.className);
			if (Is.emptyString(value)) {
				if (this.className.equals("java.util.Map"))
					return new HashMap();
				else if (this.className.equals("java.util.Collection"))
					return new ArrayList();
				else if (this.className.equals("java.util.List"))
					return new ArrayList();
				else
					return clase.newInstance();
			}
			else {			
				Constructor constructor = clase.getConstructor(PARAMETER_TYPES);
				Object [] values = { value }; 
				return constructor.newInstance(values);  
			}
		}		
		catch (NoSuchMethodException ex) {
			throw new XavaException("session_object_value_invalid", name, value, this.className, this.className);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(),ex);
			throw new XavaException("create_error", name);
		}
		catch (NoClassDefFoundError ex) {						
			if (ex.getMessage().indexOf("javax/ejb") >= 0) {
				throw new NoClassDefFoundError(ex.getMessage() + ": " + XavaResources.getString("make_sure_ejb_jar_in_lib"));
			}
			throw ex;
		}		
	}

	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

}
