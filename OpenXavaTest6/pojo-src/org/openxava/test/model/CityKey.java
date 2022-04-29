package org.openxava.test.model;

import java.io.*;

import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * Create on 16/01/2012 (09:55:27)
 * @author Ana Andres
 */
public class CityKey implements Serializable {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="STATE", referencedColumnName="ID")
	@DescriptionsList
	private State state;
	
	private int code;

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CityKey other = (CityKey) obj;
		if (code != other.code)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}
	
	
}
