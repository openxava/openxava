package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.test.actions.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(WarehouseKey.class)
@Tab( 
	filter=org.openxava.test.filters.LimitZoneFilter.class,
	baseCondition="${zoneNumber} <= ?"
)
@Views({
	@View (name="KeyInGroup", members="key [zoneNumber, number]; name"),
	@View (name="WithOnChangeZone"),
	@View (name="Number", members="number"), 
	@View (name="WithoutZone", members="number, name")
})
public class Warehouse {
	
	@Id 
	// Column is also specified in WarehouseKey because a bug in Hibernate, see
	// http://opensource.atlassian.com/projects/hibernate/browse/ANN-361	
	@Column(length=3, name="ZONE") 
	@OnChange(forViews="WithOnChangeZone", value=OnChangeVoidAction.class)			
	private int zoneNumber;	
	
	@Id @Column(length=3)
	private int number;
	
	@Column(length=40) @Required
	private String name;
	
	// The value is produces by the editor, this property always is 0. For testing purpose 
	@Transient @Stereotype("CURRENT_TIME_MILLIS")
	private long time; 

	public static Warehouse findByZoneNumberNumber(int zoneNumber, int number) throws NoResultException { 	 			
 		Query query = XPersistence.getManager().createQuery("from Warehouse as o where o.zoneNumber = :zoneNumber and number = :number"); 
		query.setParameter("zoneNumber", zoneNumber); 
		query.setParameter("number", number); 
 		return (Warehouse) query.getSingleResult();
	} 

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getZoneNumber() {
		return zoneNumber;
	}

	public void setZoneNumber(int zoneNumber) {
		this.zoneNumber = zoneNumber;
	}

	public long getTime() {
		return time;
	}


	public void setTime(long time) {
		this.time = time;
	}
			
}
