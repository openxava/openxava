package org.openxava.test.model;

import org.openxava.jpa.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
public class Orphan extends Nameable {
	 
	@ManyToOne
	private Orphanage orphanage;
	
	static public int count() {
		Query query = XPersistence.getManager().createQuery("select count(o) from Orphan o");
		return ((Number) query.getSingleResult()).intValue();
	}

	public Orphanage getOrphanage() {
		return orphanage;
	}

	public void setOrphanage(Orphanage orphanage) {
		this.orphanage = orphanage;
	}
	
}
