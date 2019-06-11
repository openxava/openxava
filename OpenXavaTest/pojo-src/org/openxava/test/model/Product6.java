package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import org.openxava.test.calculators.*;
import org.openxava.test.validators.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="PRODUCT")
public class Product6 {
	
	@Id @Column(length=10) 
	private long number;
	
	@Column(length=40) @Required
	private String description;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="FAMILY")
	@DescriptionsList   
	private Family2 family;	
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="SUBFAMILY")
	@DescriptionsList
	@DefaultValueCalculator(
		value=org.openxava.calculators.IntegerCalculator.class,
		properties={ @PropertyValue(name="value", from="family.number") }		
	)
	private Subfamily2 subfamily;
	
	@Stereotype("MONEY") @Required
	@DefaultValueCalculator(value=DefaultProductPriceCalculator.class, properties=
		@PropertyValue(name="familyNumber", from="family.number")
	)
	@PropertyValidator(UnitPriceValidator.class)
	private BigDecimal unitPrice;

	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public Family2 getFamily() {
		return family;
	}

	public void setFamily(Family2 family) {
		this.family = family;
	}

	public Subfamily2 getSubfamily() {
		return subfamily;
	}

	public void setSubfamily(Subfamily2 subfamily) {
		this.subfamily = subfamily;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

}
