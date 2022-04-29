package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Warehouse2Building extends Nameable {
	
	@ManyToOne
	private Warehouse2 warehouse;

	public Warehouse2 getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse2 warehouse) {
		this.warehouse = warehouse;
	}

}
