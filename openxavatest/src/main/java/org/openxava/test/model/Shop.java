package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.model.*;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class Shop extends Identifiable {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
