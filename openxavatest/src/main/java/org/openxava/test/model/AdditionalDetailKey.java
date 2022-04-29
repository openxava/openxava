package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */
public class AdditionalDetailKey implements java.io.Serializable {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SERVICE")
	private Service service;
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
		return "AdditionalDetailKey::" + service.getNumber() + ":" + counter;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

}
