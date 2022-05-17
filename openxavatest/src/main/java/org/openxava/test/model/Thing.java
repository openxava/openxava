package org.openxava.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.openxava.annotations.Hidden;
import org.openxava.annotations.Required;

/**
 * Create on 15/04/2010 (10:45:32)
 * @author Ana Andres
 */

@Entity
public class Thing {
	
	@Id @Column(length=5, name="IDTHING") @Hidden
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer number;
	
	@Column(length=20) @Required
	private String name;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
