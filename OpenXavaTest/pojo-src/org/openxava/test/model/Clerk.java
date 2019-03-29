package org.openxava.test.model;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.openxava.annotations.*;
import org.openxava.jpa.*;

/**
 * @author Javier Paniza
 */

@Entity
@IdClass(ClerkKey.class)
public class Clerk {
	
	@Id @Required 
	// Column is also specified in ClerkKey because a bug in Hibernate, see
	// http://opensource.atlassian.com/projects/hibernate/browse/ANN-361
	@Column(length=3, name="ZONE") 
	private int zoneNumber;
	
	@Id @Required
	// Column is also specified in ClerkKey because a bug in Hibernate, see
	// http://opensource.atlassian.com/projects/hibernate/browse/ANN-361
	@Column(length=3, name="OFFICE")
	private int officeNumber;
	
	@Id  @Required 
	@Column(length=3, name="NUMBER")
	private int number;
	
	@Required @Column(length=40)
	private String name;

	// We test the two: java.sql.Time and TIME stereotype
	private java.sql.Time arrivalTime;
	@Stereotype("TIME")
	private String endingTime;
	
	// For testing a String property stored in a binary field in database
	@Stereotype("MEMO") @Type(type="org.openxava.types.StringArrayBytesType")		
	private String comments;
	
	private Boolean onVacation; 
	
 	public static Clerk findByZoneNumberOfficeNumberNumber(int zoneNumber,int officeNumber,int number) throws NoResultException { 	 			
 		Query query = XPersistence.getManager().createQuery("from Clerk as o where o.zoneNumber = :zoneNumber and officeNumber = :officeNumber and number = :number"); 
		query.setParameter("zoneNumber", new Integer(zoneNumber)); 
		query.setParameter("officeNumber", new Integer(officeNumber)); 
		query.setParameter("number", new Integer(number)); 
 		return (Clerk) query.getSingleResult();
 	} 


	public java.sql.Time getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(java.sql.Time arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getEndingTime() {
		return endingTime;
	}

	public void setEndingTime(String endingTime) {
		this.endingTime = endingTime;
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

	public int getOfficeNumber() {
		return officeNumber;
	}

	public void setOfficeNumber(int officeNumber) {
		this.officeNumber = officeNumber;
	}

	public int getZoneNumber() {
		return zoneNumber;
	}

	public void setZoneNumber(int zoneNumber) {
		this.zoneNumber = zoneNumber;
	}


	public Boolean getOnVacation() {
		return onVacation;
	}


	public void setOnVacation(Boolean onVacation) {
		this.onVacation = onVacation;
	}
		
}
