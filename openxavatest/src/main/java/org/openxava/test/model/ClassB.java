/**
 * 
 */
package org.openxava.test.model;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.openxava.annotations.View;

/**
 * @author Federico Alcantara
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
