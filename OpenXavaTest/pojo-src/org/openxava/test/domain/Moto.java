package org.openxava.test.domain;

import javax.persistence.*;
import org.openxava.model.*;

/**
 * To test a model class inside a package with a name other than 'model' or 'modelo',
 * 
 * @author Javier Paniza 
 */

@Entity
public class Moto extends Identifiable {
	
	private String make;
	private String model;
	
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}

}
