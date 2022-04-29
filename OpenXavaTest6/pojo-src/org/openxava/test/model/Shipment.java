package org.openxava.test.model;

import java.sql.*;
import java.util.*;

import javax.persistence.*;

import org.hibernate.annotations.Parameter;
import org.openxava.annotations.*;
import org.openxava.jpa.*;

/**
 * Example of enum with converter (Hibernate Type) used as key. <p>
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(ShipmentKey.class)
@Tab(properties="number, description, time")
@View(name="SeparatedTime") 
public class Shipment {
	
	@Id @Required
	// @org.hibernate.annotations.Type is also specified in ShipmentKey 
	// because a bug in Hibernate, see http://opensource.atlassian.com/projects/hibernate/browse/ANN-361	
	@org.hibernate.annotations.Type(type="org.openxava.types.EnumLetterType", 
		parameters={
			@Parameter(name="letters", value="IE"), 
			@Parameter(name="enumType", value="org.openxava.test.model.Shipment$Type")
		}
	)			
	private Type type;
	public enum Type { INTERNAL, EXTERNAL }
	
	@Id @Required
	// @org.hibernate.annotations.Type is also specified in ShipmentKey 
	// because a bug in Hibernate, see http://opensource.atlassian.com/projects/hibernate/browse/ANN-361		
	@org.hibernate.annotations.Type(type="org.openxava.types.Base1EnumType", 
		parameters={			
			@Parameter(name="enumType", value="org.openxava.test.model.Shipment$Mode")
		}
	)	
	private Mode mode;
	public enum Mode { SLOW, MEDIUM, FAST }
	
	@Id @Required
	@Column(length=5)
	private int number;
	
	@Column(length=50) @Required
	private String description;
	
	@Editor(forViews="SeparatedTime", value="DateTimeSeparatedCalendar")
	private Timestamp time;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="CUSTOMERCONTACT", referencedColumnName="CUSTOMER_NUMBER")
	})
	@ReferenceView("Simple")
	private CustomerContactPerson customerContactPerson;
	
		
 	public static Collection findAll()  { 			
		javax.persistence.Query query = org.openxava.jpa.XPersistence.getManager().createQuery("from Shipment as o"); 
 		return query.getResultList();  		
 	}
 	
 	public static Collection findByMode(Mode mode)  { 		 			
 		javax.persistence.Query query = XPersistence.getManager().createQuery("from Shipment as o where o.mode = :mode"); 
		query.setParameter("mode", mode); 
 		return query.getResultList();  		 		
 	}
 	
 	public static Collection findByMode(int ordinal)  {
 		return findByMode(Mode.values()[ordinal]);
 	} 	
 	
 	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Mode getMode() {
		return mode;
	}
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public CustomerContactPerson getCustomerContactPerson() {
		return customerContactPerson;
	}

	public void setCustomerContactPerson(CustomerContactPerson customerContactPerson) {
		this.customerContactPerson = customerContactPerson;
	};

}
