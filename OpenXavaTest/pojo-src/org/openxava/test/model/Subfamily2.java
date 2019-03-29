package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

/**
 * Subfamily2 has not oid,
 * like the typical number/description table.
 * Subfamily2 use reference against family, instead stereotype
 * It go against the same table that Subfamily, because we does
 * not save hence the oid don't disturb.
 *
 * @author Javier Paniza
 */

@Entity
@Tab(properties="number, description, remarks, family.number, family.description")
@Views({ 
	@View(name="WithFamilyNoFrame", members="family"),
	@View(name="Number", members="number") 
})
public class Subfamily2 {
	
	@Id @Column(length=3)
	private int number;

	@ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="FAMILY")
	@DescriptionsList(notForViews="WithFamilyNoFrame",
			descriptionProperties="number, description")	
	@NoFrame(forViews="WithFamilyNoFrame") // For testing a case in PrettyLayout.txt
	@ReferenceView(forViews="WithFamilyNoFrame", value="OneLine") // For testing a case in PrettyLayout.txt 
	private Family2 family;
	
	@Column(length=40) @Required
	private String description;
	
	@Stereotype("MEMO") @Column(length=400)
	private String remarks;
	
	public Collection<Product2> getProductsValues() {
		javax.persistence.Query query = XPersistence.getManager().createQuery("from Product2 where subfamily.number = :subfamilyNumber");
		query.setParameter("subfamilyNumber", getNumber());
		return query.getResultList();					
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Family2 getFamily() {
		return family;
	}

	public void setFamily(Family2 family) {
		this.family = family;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
