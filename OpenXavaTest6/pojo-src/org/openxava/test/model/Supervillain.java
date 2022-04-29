package org.openxava.test.model;

import org.openxava.model.*;
import org.openxava.annotations.*;
import javax.persistence.*;

@Entity
@Table(name="SUPERS")
@Tab(properties="name, superheroe.name")
public class Supervillain extends Identifiable {
	
	@Column(length=40) @Required
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Superheroe superheroe;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Superheroe getSuperheroe() {
		return superheroe;
	}

	public void setSuperheroe(Superheroe superheroe) {
		this.superheroe = superheroe;
	}

}
