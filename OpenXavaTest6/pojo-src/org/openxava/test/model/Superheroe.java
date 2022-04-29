package org.openxava.test.model;

import org.openxava.annotations.*;
import org.openxava.model.*;

import javax.persistence.*;

@Entity
@Table(name="SUPERS")
@Tab(defaultOrder="${name} asc")
public class Superheroe extends Identifiable {

	@Stereotype("NO_HTML_IN_LIST")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
