package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * WARNING! DON'T ADD ANY @OnChange, @Depends, @DefaultValueCalculator WITH "from" PROPERTIES @DescriptionList WITH depends. 
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="PRODUCT")
public class Product6 {
	
	// WARNING! DON'T ADD ANY @OnChange, @Depends, @DefaultValueCalculator WITH "from" PROPERTIES @DescriptionList WITH depends.
	@Id @Column(length=10) 
	private long number;
	
	// WARNING! DON'T ADD ANY @OnChange, @Depends, @DefaultValueCalculator WITH "from" PROPERTIES @DescriptionList WITH depends.
	@Column(length=40) @Required
	private String description;
	
	// WARNING! DON'T ADD ANY @OnChange, @Depends, @DefaultValueCalculator WITH "from" PROPERTIES @DescriptionList WITH depends.
	@ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="FAMILY")
	@DescriptionsList   
	private Family2 family;	
	
	// WARNING! DON'T ADD ANY @OnChange, @Depends, @DefaultValueCalculator WITH "from" PROPERTIES @DescriptionList WITH depends.
	@ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="SUBFAMILY")
	@DescriptionsList
	@DefaultValueCalculator(
		value=org.openxava.calculators.IntegerCalculator.class,
		properties={ @PropertyValue(name="value", from="family.number") }		
	)
	private Subfamily2 subfamily;
	
	
	// WARNING! DON'T ADD ANY @OnChange, @Depends, @DefaultValueCalculator WITH "from" PROPERTIES @DescriptionList WITH depends.
		
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

}
