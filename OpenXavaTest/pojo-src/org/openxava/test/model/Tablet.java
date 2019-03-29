package org.openxava.test.model;

import javax.persistence.*;

@Entity
public class Tablet extends Computer {

	private int screenSize;

	public int getScreenSize() {
		return screenSize;
	}

	public void setScreenSize(int screenSize) {
		this.screenSize = screenSize;
	}
	
}
