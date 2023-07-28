/**
 * 
 */
package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.model.*;

/**
 * @author Federico Alcantara
 *
 */
@Entity
// With no table in database, to test a case
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
