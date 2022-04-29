package org.openxava.test.model;

import javax.persistence.*;

@Entity
public class Desktop extends Computer {
	
	private int hardDiskCapacity;

	public int getHardDiskCapacity() {
		return hardDiskCapacity;
	}

	public void setHardDiskCapacity(int hardDiskCapacity) {
		this.hardDiskCapacity = hardDiskCapacity;
	}

}
