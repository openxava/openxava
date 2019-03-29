/**
 * 
 */
package org.openxava.test.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import org.openxava.model.Identifiable;

/**
 * @author Federico Alcantara
 *
 */
@Entity
public class ClassA extends Identifiable {
	@Embedded
	private ClassB b;

	public ClassB getB() {
		return b;
	}

	public void setB(ClassB b) {
		this.b = b;
	}
	
}
