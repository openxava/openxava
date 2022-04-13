package org.openxava.model.transients;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class Import implements java.io.Serializable { 
	
	@Hidden
	@Stereotype("FILE")
	private String data;
	
	@Hidden
	private String modelName;
	
	@ElementCollection @EditOnly
	private Collection<ImportColumn> columns;
	
	/**
	 * @since 6.3.2
	 */
	public static String encodeSeparators(String text) { 
		return text.replace(XavaPreferences.getInstance().getCSVSeparator(), "__SPRTR__"); 
	}

	/**
	 * @since 6.3.2
	 */	
	public static String decodeSeparators(String text) { 
		return text.replace("__SPRTR__", XavaPreferences.getInstance().getCSVSeparator()); 
	}
	
	public Collection<ImportColumn> getColumns() {
		return columns;
	}

	public void setColumns(Collection<ImportColumn> columns) {
		this.columns = columns;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

}
