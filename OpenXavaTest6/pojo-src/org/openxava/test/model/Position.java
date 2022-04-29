package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

@Entity
@Table(name="TPOSITION") // It's not for testing something, it's because Hypersonic does not like POSITION as table name
public class Position extends Identifiable {
	
	@Column(length=40) @Required
	private String name;
	
	@Column(scale=4, precision=4) 
	private float axisX;
	
	@Column(scale=7, precision=7) 
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
