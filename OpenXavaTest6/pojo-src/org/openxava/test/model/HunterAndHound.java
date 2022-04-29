package org.openxava.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.View;
import org.openxava.model.Identifiable;
import org.openxava.test.calculators.NextNumberForHunterAndHound;

@Entity
@View(members="name, number")
abstract public class HunterAndHound extends Identifiable {
	
	@Column(length=25)
	private String name;
	
	@ReadOnly
	@DefaultValueCalculator(value=NextNumberForHunterAndHound.class)
	private int number;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}		
}
