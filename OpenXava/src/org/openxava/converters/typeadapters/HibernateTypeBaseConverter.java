package org.openxava.converters.typeadapters;

import java.util.*;

import org.hibernate.usertype.*;

/**
 * Base class for adapters for using Hibernate composite types as converters in OpenXava.
 
 * @author Javier Paniza
 */

abstract public class HibernateTypeBaseConverter {
	
	private Properties parameters;
	private Object hibernateType;
	private String type;

	
	protected Object getHibernateType() throws Exception {
		if (hibernateType == null) {			
			hibernateType = Class.forName(type).newInstance();
			if (this.parameters != null && hibernateType instanceof ParameterizedType) {
				((ParameterizedType) hibernateType).setParameterValues(parameters);
			}
		}
		return hibernateType;
	}
	
	
	
	public void setParameters(String parameters) {
		this.parameters = new Properties();
		for (String parameter: parameters.split("\",")) {
			String [] nameValue = parameter.split("=\"");
			this.parameters.setProperty(nameValue[0], nameValue[1]);
		}
	}

	public String getType() {
		return type;
	}
		

	public void setType(String type) {
		this.type = type;
		this.hibernateType = null;
	}
	
}
