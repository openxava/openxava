package org.openxava.test.model;

import org.openxava.jpa.*;

import jakarta.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
public class Orphan extends Nameable {
	 
	@ManyToOne
	private Orphanage orphanage;
	
	static public int count() {
		TypedQuery<Long> query = XPersistence.getManager().createQuery("SELECT COUNT(o) FROM Orphan o", Long.class);
		return ((Number) query.getSingleResult()).intValue();
	}

	public Orphanage getOrphanage() {
		return orphanage;
	}

	public void setOrphanage(Orphanage orphanage) {
		this.orphanage = orphanage;
	}
	
}
