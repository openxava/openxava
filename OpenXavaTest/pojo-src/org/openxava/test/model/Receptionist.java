package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Receptionist {
	
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="DELIVERYPLACE")
	private DeliveryPlace deliveryPlace;
	
	// It's calculAted in the method calculateOID
	@Id @Hidden 
	private int oid;
	
	@Column(length=30) @Required
	private String name;
	
 	public static Collection findAll()  { 			
 		javax.persistence.Query query = org.openxava.jpa.XPersistence.getManager().createQuery("from Receptionist as o"); 
 		return query.getResultList();  		
 	} 

	
	@PrePersist
	private void calculateOID() {
		// Thus we can calculate an oid in a custom way

		// Generally its better to use the EJB3 standard techniques for
		// generating OIDs, but sometimes it's needed to execute custom
		// code, specially when you have to work with legate databases.
		try {
			NextIntegerCalculator calculator = new NextIntegerCalculator();
			calculator.setModel("Customer.Receptionist");
			calculator.setProperty("oid");
			calculator.setConnectionProvider(DataSourceConnectionProvider.getByComponent("Customer"));
			this.oid = ((Number) calculator.calculate()).intValue();
		}
		catch (Exception ex) {
			throw new PersistenceException("Problem calculating OID of Receptionist");
		}
	}


	public DeliveryPlace getDeliveryPlace() {
		return deliveryPlace;
	}

	public void setDeliveryPlace(DeliveryPlace deliveryPlace) {
		this.deliveryPlace = deliveryPlace;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOid() {
		return oid;
	}

	public void setOid(int oid) {
		this.oid = oid;
	}
	
}
