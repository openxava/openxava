package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */


@Entity
@Table(name="SERVICEDETAIL")
@IdClass(AdditionalDetailKey.class)
public class AdditionalDetail {

	// JoinColumn is also specified in AditionalDetailKey because 
	// a bug in Hibernate, see http://opensource.atlassian.com/projects/hibernate/browse/ANN-361	
	@Id @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SERVICE")
	private Service service;
	
	// It's calculAted in the method calculateCounter
	@Id @Hidden
	private int counter;
	
	@Required @Stereotype("SUBFAMILY")
	private int subfamily;
	
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="TYPE") 
	@DescriptionsList(
		depends="family, additionalDetails.subfamily", 
		condition="${family} = ? and ${subfamily} = ?"
	)
	private ServiceType type;
	
	@PrePersist
	private void calculateCounter() {
		// Thus we can calculate an id in a custom way
		
		// In EJB2 and Hibernate version we use a counter (instead of
		// a currentTimeMillis), but in EJB3 version of OpenXava this is not supported,
		// because it's better to use the standard oid generation of JPA, and rarely
		// to receive a sequential counter from UI would be useful.
		
		// That is the technique of org.openxava.calculators.IAggregateOidCalculator
		// is deprecated in OX3 
		counter = new Long(System.currentTimeMillis()).intValue();
	}


	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}


	public int getSubfamily() {
		return subfamily;
	}


	public void setSubfamily(int subfamily) {
		this.subfamily = subfamily;
	}


	public ServiceType getType() {
		return type;
	}


	public void setType(ServiceType type) {
		this.type = type;
	}
	
}
