package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * 
 * @author Javier Paniza
 */

@Embeddable
@View(members="subfamily; type; free")
public class ServiceDetail {

	@Stereotype("SUBFAMILY")
	private int subfamily;
	
	@Stereotype("SERVICE_TYPE")
	private int type;
	
	// To test overlapping with property.
	// ManyToOne inside an Embeddable is not supported by JPA 1.0 (see at 9.1.34),
	// but Hibernate implementation supports it.
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name="TYPE", insertable=false, updatable=false)
	private ServiceType typeRef;
	
	private boolean free;

	public int getSubfamily() {
		return subfamily;
	}

	public void setSubfamily(int subfamily) {
		this.subfamily = subfamily;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ServiceType getTypeRef() {
		return typeRef;
	}

	public void setTypeRef(ServiceType typeRef) {
		this.typeRef = typeRef;
		this.type = typeRef == null?0:typeRef.getNumber();
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}
}
