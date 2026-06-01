package org.openxava.test.model;

import java.util.*;

import jakarta.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.test.annotations.*;

import lombok.*;

/**
 * Carrier with custom editors for reference and collection using annotations.
 * 
 * @author Javier Paniza
 */
@Entity @Getter @Setter @Table(name="CARRIER")
public class CustomCarrier {
	
	@Id @Column(length=5)
	private int number;
	
	@Required @Column(length=40) 	
	private String name;
		
	@ManyToOne(optional=false) 
	@CustomWarehouse
	private Warehouse warehouse;

	@CarriersNames
	public Collection<Carrier> getFellowCarriers() { 
		TypedQuery<Carrier> query = XPersistence.getManager().createQuery("SELECT c FROM Carrier c WHERE " +
			"NOT (c.number = :number) " +
			"order by c.number", Carrier.class);  
		query.setParameter("number",  getNumber());
		return query.getResultList();
	}

}
