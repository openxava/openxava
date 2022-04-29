/**
 * 
 */
package org.openxava.test.model;

import javax.persistence.Entity;

import org.openxava.model.Identifiable;

/**
 * @author Federico Alcantara
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
