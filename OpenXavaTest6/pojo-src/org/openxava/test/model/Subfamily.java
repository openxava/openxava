package org.openxava.test.model;

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@View(members=
	"number;" +
	"data {" +
	"	familyNumber;" +
	"	family;" +
	"	description;" +
	"	remarks" +
	"}"	
)
@Tab(name="CompleteSelect",
	properties="number, description, family",	
	/* For JPA */   
	baseCondition = // JPA query using e as alias for main entity. Since v4.5
		"select e.number, e.description, f.description " +
		"from Subfamily e, Family f " +
		"where e.familyNumber = f.number"
								
	/* For Hypersonic      	
	baseCondition = // With SQL until v4.4.x
		"select ${number}, ${description}, FAMILY.DESCRIPTION " +
		"from   XAVATEST.SUBFAMILY, XAVATEST.FAMILY " +
		"where  SUBFAMILY.FAMILY = FAMILY.NUMBER"
	*/	
										
	/* For AS/400 	    	
	baseCondition = // With SQL until v4.4.x
		"select ${number}, ${description}, XAVATEST.FAMILY.DESCRIPTION " +
		"from   XAVATEST.SUBFAMILY, XAVATEST.FAMILY " +
		"where  XAVATEST.SUBFAMILY.FAMILY = XAVATEST.FAMILY.NUMBER"
	*/		
)
public class Subfamily {
	
	@Id @GeneratedValue(generator="system-uuid") @Hidden 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String oid;
	
	@Column(length=3) @Required @Stereotype("ZEROS_FILLED")
	private int number;
	
	@Required @Stereotype("FAMILY") @Column(name="FAMILY")
	private int familyNumber;
	
	@Column(length=40) @Required 
	private String description;
	
	@Column(length=400) @Stereotype("MEMO") 
	@org.hibernate.annotations.Type(type="org.openxava.types.NotNullStringType")
	private String remarks;
	
	@Transient @Column(length=40) @Hidden  
	private String family;	// Only for column description in tab 
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFamilyNumber() {
		return familyNumber;
	}

	public void setFamilyNumber(int familyNumber) {
		this.familyNumber = familyNumber;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getFamily() {
		return family; 
	}
	public void setFamily(String family) { 
		this.family = family;
	}

	
}
