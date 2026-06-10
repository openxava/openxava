/**
 * 
 */
package org.openxava.test.model;

import jakarta.persistence.Entity;

import org.openxava.model.Identifiable;

/**
 * @author Federico Alcántara
 *
 */
@Entity
public class ClassC extends Identifiable {
	private String nameC;

	public String getNameC() {
		return nameC;
	}

	public void setNameC(String nameC) {
		this.nameC = nameC;
	}
}
