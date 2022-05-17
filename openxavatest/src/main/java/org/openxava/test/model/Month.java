package org.openxava.test.model;

/**
 * 
 * @author Javier Paniza
 */

public class Month {
	
	private String name;
	private int days;
	
	public Month(String name, int days) {
		this.name = name;
		this.days = days;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public int getDays() {
		return days;
	}

}
