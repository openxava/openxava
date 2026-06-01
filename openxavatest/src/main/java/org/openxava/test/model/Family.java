package org.openxava.test.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;

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
	
		 
	@Id @GeneratedValue(strategy = GenerationType.UUID) @Hidden
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
