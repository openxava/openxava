package org.openxava.test.model;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */
public class AdditionalDetailKey implements java.io.Serializable {

	private int service;
	@Hidden
	private int counter;
	
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
		return "AdditionalDetailKey::" + service + ":" + counter;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getService() {
		return service;
	}

	public void setService(int service) {
		this.service = service;
	}

}
