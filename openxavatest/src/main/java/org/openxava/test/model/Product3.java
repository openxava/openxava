package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.test.actions.*;

/**
 * Stores oid of family and not the code. <p>
 *  
 * @author Javier Paniza
 */

@Entity
@Views({
	@View( members="number; family; description; subfamily1; subfamily2; comments;" ),	
	@View( name="WithDescriptionsList",
		members="number; family; description; subfamily1; subfamily2; comments;"
	),
	@View( name="WithGroup",members = 
		"number, family;" + 
		"description [" +
		"	description; " +
		"	comments" +
		"]"
	)
})
public class Product3 {

	@DefaultValueCalculator( 
		value=NextIntegerCalculator.class, 
		properties = {
			@PropertyValue(name="model", value="Product3"),
			@PropertyValue(name="property", value="number")
		}
	)
	@Id @Column(length=10) @Required
	private long number;

	@Column(length=40) @Required
	@OnChange(forViews="WithGroup", value=OnChangeProduct3DescriptionAction.class)
	private String description;

	@ManyToOne(fetch=FetchType.LAZY) @DescriptionsList(forViews="WithDescriptionsList, WithGroup") 
	@JoinColumn(name="FAMILY")
	@OnChange(forViews="WithGroup, DEFAULT", value=OnChangeFamilyAction.class)
	private Family family;
	
	@Stereotype("MEMO")
	private String comments;

	// WARNING! @AssociationOverrides is not officially supported in JPA 1.0 
	@Embedded @Required
	@AssociationOverrides({ 
		@AssociationOverride(name="family", joinColumns=@JoinColumn(name="FAMILY1")),
		@AssociationOverride(name="subfamily", joinColumns=@JoinColumn(name="SUBFAMILY1"))
	})
	private SubfamilyInfo subfamily1;
	
	// WARNING! @AssociationOverrides is not officially supported in JPA 1.0
	@Embedded @Required
	@AssociationOverrides({ 
		@AssociationOverride(name="family", joinColumns=@JoinColumn(name="FAMILY2")),
		@AssociationOverride(name="subfamily", joinColumns=@JoinColumn(name="SUBFAMILY2"))
	})	
	private SubfamilyInfo subfamily2;
	
	
	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Family getFamily() {
		return family;
	}

	public void setFamily(Family family) {
		this.family = family;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	
	public SubfamilyInfo getSubfamily1() {	
		return subfamily1;
	}

	public void setSubfamily1(SubfamilyInfo subfamily1) {
		this.subfamily1 = subfamily1;
	}
	
	public SubfamilyInfo getSubfamily2() {
		return subfamily2;
	}

	public void setSubfamily2(SubfamilyInfo subfamily2) {
		this.subfamily2 = subfamily2;
	}	
	
}
