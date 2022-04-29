package org.openxava.test.base;

import java.util.*;
import javax.persistence.*;

/**
 * 
 * @author Sebastien Diot
 */
@MappedSuperclass
public abstract class AbstractToto<T extends AbstractToto<?>> {
	
	@Id
	private int id;
	
	@Column(length = 50)
	private String name;
	
	@ManyToMany
	private Collection<T> friends;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFriends(Collection<T> friends) {
		this.friends = friends;
	}

	public Collection<T> getFriends() {
		return friends;
	}
	
}