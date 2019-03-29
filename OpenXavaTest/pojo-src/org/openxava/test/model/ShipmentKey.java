package org.openxava.test.model;

import org.hibernate.annotations.Parameter;
import org.openxava.test.model.Shipment.*;

/**
 * 
 * @author Javier Paniza
 */
public class ShipmentKey implements java.io.Serializable {

	@org.hibernate.annotations.Type(type="org.openxava.types.EnumLetterType", 
		parameters={
			@Parameter(name="letters", value="IE"), 
			@Parameter(name="enumType", value="org.openxava.test.model.Shipment$Type")
		}
	)	
	private Type type;
	@org.hibernate.annotations.Type(type="org.openxava.types.Base1EnumType", 
		parameters={			
			@Parameter(name="enumType", value="org.openxava.test.model.Shipment$Mode")
		}
	)	
	private Mode mode;
	private int number;
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return obj.toString().equals(this.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "ShipmentKey::" + type + ":" + mode + ":" + number;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
