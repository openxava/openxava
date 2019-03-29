package org.openxava.test.model;

import org.openxava.annotations.*;
import java.util.*;
import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@View(members="orphanage [ name; orphans ]") 
public class Orphanage extends Nameable {

	@OneToMany(orphanRemoval=true, mappedBy="orphanage")
	@AsEmbedded 
	private Collection<Orphan> orphans;

	public Collection<Orphan> getOrphans() {
		return orphans;
	}

	public void setOrphans(Collection<Orphan> orphans) {
		this.orphans = orphans;
	}
	
}
