package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

/**
 *
 * @author Javier Paniza
 */
@Entity
public class Hobbyist extends Nameable {

	@ManyToMany(mappedBy="hobbyists")
	private Collection<Hobby> hobbies;

	public Collection<Hobby> getHobbies() {
		return hobbies;
	}

	public void setHobbies(Collection<Hobby> hobbies) {
		this.hobbies = hobbies;
	}
	
}
