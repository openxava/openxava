package org.openxava.tab.impl;

import java.util.*;



import org.openxava.converters.*;
import org.openxava.mapping.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

class TabConverter implements java.io.Serializable {
	
	private Map cmpFields;
	private int index;
	private String propertyName;
	private IConverter converter;
	private IMultipleConverter multipleConverter;
	

	public TabConverter(String propertyName, int index, IConverter converter)
		throws XavaException {
		this.index = index;
		this.propertyName = propertyName;
		this.converter = converter;
	}
	
	public TabConverter(String propertyName, int index, IMultipleConverter converter, Collection cmpFields, String [] columns, String table)
		throws XavaException {
		this.index = index;
		this.propertyName = propertyName;
		this.multipleConverter = converter;
		Iterator it = cmpFields.iterator();
		this.cmpFields = new HashMap();
		List columnsList = Arrays.asList(columns);				
		while (it.hasNext()) {
			CmpField field =  (CmpField) it.next();
			int cmpIndex = columnsList.indexOf(table + "." + field.getColumn());
			this.cmpFields.put(field, new Integer(cmpIndex));
		}		 
	}
		
	public Collection getCmpFields() {
		return cmpFields == null?Collections.EMPTY_SET:cmpFields.keySet();	
	}
	
	public int getIndex(CmpField field) throws ElementNotFoundException {
		if (this.cmpFields == null) {
			throw new ElementNotFoundException("column_multiple_not_found", field.getColumn()); 
		}
		Integer index = (Integer) this.cmpFields.get(field);
		if (index == null) {
			throw new ElementNotFoundException("column_multiple_not_found", field.getColumn());
		}
		return index.intValue();
	}
	
	/**
	 * Returns the calculador.
	 * @return ICalculador
	 */
	public IConverter getConverter() {
		return converter;
	}
	
	public IMultipleConverter getMultipleConverter() {
		return multipleConverter;
	}
	
	public boolean hasMultipleConverter() {
		return multipleConverter != null;
	}

	/**
	 * Returns the indice.
	 * @return int
	 */
	public int getIndex() {
		return index;
	}


	/**
	 * Returns the nombrePropiedad.
	 * @return String
	 */
	public String getPropertyName() {
		return propertyName;
	}
	
	public String toString() {
		return "TabConverter: " + propertyName + ", " + index + ", " + (converter==null?"NULL":converter.getClass().getName());
	}

}

