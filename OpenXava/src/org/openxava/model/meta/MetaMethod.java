package org.openxava.model.meta;

import java.io.*;



import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class MetaMethod implements Serializable {
	
	private String name;
	private String typeName;
	private String arguments;
	private String exceptions;
	private MetaCalculator metaCalculator;
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String string) {
		name = string;
	}	
			
	public String getArguments() {
		return arguments==null?"":arguments;
	}

	public String getExceptions() {
		return exceptions;
	}
	
	public boolean hasExceptions() {
		return !Is.emptyString(exceptions);
	}
	
	public boolean hasArguments() {
		return !Is.emptyString(arguments);
	}
	
	public String getTypeName() {
		return typeName;
	}

	public void setArguments(String string) {
		arguments = string;
	}

	public void setExceptions(String string) {
		exceptions = string;
	}

	public void setTypeName(String string) {
		typeName = string;
	}

	public MetaCalculator getMetaCalculator() {
		return metaCalculator;
	}

	public void setMetaCalculator(MetaCalculator calculator) {
		metaCalculator = calculator;
	}
	
}