/**
 * 
 */
package org.openxava.test.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import org.openxava.model.Identifiable;

/**
 * @author federico
 *
 */
@MappedSuperclass
public class BaseSitting extends Identifiable implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
