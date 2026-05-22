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
	
	@DecimalMax("0.9999")
	private float axisX;

	@DecimalMax("0.9999999")
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
