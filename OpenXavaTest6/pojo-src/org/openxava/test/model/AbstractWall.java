package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;

import org.openxava.model.*;

/**
 *
 * @author Sebastien Diot
 */

@MappedSuperclass
public abstract class AbstractWall<E extends AbtractWallEntry<?>> extends Identifiable {
	
	@OneToMany(mappedBy="wall")
	private Collection<E> entries;

	public void setEntries(Collection<E> entries) {
		this.entries = entries;
	}

	
	public Collection<E> getEntries() {
		if (entries == null) {
			entries = new ArrayList<E>();
		}
		return entries;
	}
	
}
