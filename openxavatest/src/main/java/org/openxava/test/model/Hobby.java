package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
public class Hobby extends Nameable {
	
	@ManyToMany
	private Collection<Hobbyist> hobbyists;

	public Collection<Hobbyist> getHobbyists() {
		return hobbyists;
	}

	public void setHobbyists(Collection<Hobbyist> hobbyists) {
		this.hobbyists = hobbyists;
	}

}
