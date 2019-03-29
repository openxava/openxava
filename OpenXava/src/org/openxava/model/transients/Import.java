package org.openxava.model.transients;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */
public class Import implements java.io.Serializable { 
	
	@Hidden
	private String data;
	
	@Hidden
	private String modelName;
	
	@ElementCollection @EditOnly
	private Collection<ImportColumn> columns;
	
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
