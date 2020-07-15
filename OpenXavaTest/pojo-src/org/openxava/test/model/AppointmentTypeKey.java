package org.openxava.test.model;

import java.io.*;
import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */
public class AppointmentTypeKey implements Serializable {
	
	@Id @Column(length=2)
	private String type;
	
	@Id
	private int level;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + level;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		AppointmentTypeKey other = (AppointmentTypeKey) obj;
		if (level != other.level)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
