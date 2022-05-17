package org.openxava.test.model;

import org.openxava.annotations.View;

/**
 * Used to assign default schema and restart modules
 * 
 * Create on 03/09/2009 (14:40:13)
 * @autor Ana Andrés
 */

@View(members="schema, SelectSchema.set()")
public class SelectSchema {
	
	private Schema schema;
	
	public enum Schema { COMPANYA, COMPANYB }
	
	public Schema getSchema() {
		return schema;
	}
	public void setSchema(Schema schema) {
		this.schema = schema;
	}
	
}
