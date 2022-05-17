package org.openxava.test.model;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.*;

import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.*;
import org.openxava.annotations.*;

/**
 * 
 * 
 * In this class we use Hibernate Validator annotations
 * for defining the size of the properties (@Max and @Length)
 * instead of the JPA one (@Column(length=)).<br>
 *
 * @author Javier Paniza
 */

@Entity
@View(members="number; description; products")
@Tab(defaultOrder="${number} asc")
public class Family {
	
		 
	@Id @GeneratedValue(generator="system-uuid") @Hidden 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String oid;
	
	@Max(999) @Required 
	private int number;
	
	@Length(max=40) @Required 
	private String description;
	
	@Transient @Stereotype("PRODUCT3")
	private long products;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public long getProducts() {
		return products;
	}

	public void setProducts(long products) {
		this.products = products;
	}

}
