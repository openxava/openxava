package org.openxava.test.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

@Entity
@Table(name="TPOSITION") // It's not for testing something, it's because Hypersonic does not like POSITION as table name
public class Position extends Identifiable {
	
	@Column(length=40) @Required
	private String name;
	
	@Digits(integer = 0, fraction = 4)
	private float axisX;
	
	@Digits(integer = 0, fraction = 7)
	private double axisY;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getAxisX() {
		return axisX;
	}

	public void setAxisX(float axisX) {
		this.axisX = axisX;
	}

	public double getAxisY() {
		return axisY;
	}

	public void setAxisY(double axisY) {
		this.axisY = axisY;
	}
	
	

}
