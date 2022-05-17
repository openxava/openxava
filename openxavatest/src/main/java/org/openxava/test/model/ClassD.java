package org.openxava.test.model;

import javax.persistence.Entity;

import org.openxava.model.Identifiable;

@Entity
public class ClassD extends Identifiable {
	private String nameD;

	public String getNameD() {
		return nameD;
	}

	public void setNameD(String nameD) {
		this.nameD = nameD;
	}
}
