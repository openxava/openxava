package org.openxava.test.model;

import java.io.*;

import org.openxava.component.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * Handmade POJO class for Family. <p>
 * 
 * Implementing IFamily is not mandatory,
 * but if you does not implement it, you cannot
 * mix generated and handmade code with references.<p>
 * 
 * @author Javier Paniza
 */
public class Family implements IFamily, Serializable {
	
	
	private String oid;
	private int number;
	private String description;
	
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
		
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private static MetaModel metaModel;
	public MetaModel getMetaModel() throws XavaException {
		if (metaModel == null) {
			metaModel = MetaComponent.get("Family").getMetaEntity(); 	
		}
		return metaModel;
	}
	
	
}
