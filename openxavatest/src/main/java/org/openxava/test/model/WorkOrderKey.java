package org.openxava.test.model;

/**
 * 
 * @author Javier Paniza
 */
public class WorkOrderKey implements java.io.Serializable {

	private Integer year;
	private Integer number;
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return obj.toString().equals(this.toString());
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
	public String toString() {
		return "WorkOrderKey::" + getYear() + ":" + getNumber();
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

}
