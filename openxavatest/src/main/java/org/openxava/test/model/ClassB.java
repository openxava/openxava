/**
 * 
 */
package org.openxava.test.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

import org.openxava.annotations.View;

/**
 * @author Federico Alcántara
 *
 */
@Embeddable
@View( members="c {c} d {d}")
public class ClassB {
	@ManyToOne( fetch=FetchType.LAZY)
	private ClassC c;
	
	@ManyToOne( fetch=FetchType.LAZY)
	private ClassD d;

	public ClassC getC() {
		return c;
	}

	public void setC(ClassC c) {
		this.c = c;
	}

	public ClassD getD() {
		return d;
	}

	public void setD(ClassD d) {
		this.d = d;
	}
}
