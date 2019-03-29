package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@View( members = "year, number, amount; description" )
@Tab( properties = "year, number, description, amount+" ) 
public class ServiceInvoice {
	
	@Id @GeneratedValue(generator="system-uuid") @Hidden 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String oid;

	@SearchKey
	@Column(length=4) @DefaultValueCalculator(CurrentYearCalculator.class)
	private int year;
	
	@SearchKey
	@Column(length=6)
	private int number;

	@Stereotype("MEMO")	
	@DefaultValueCalculator( // This is needed to test ServiceTest.testSearchKey() with the search key with a dependent property 
		value=ConcatCalculator.class, 
		properties={
			@PropertyValue(name="string1", value="Service invoice of "),
			@PropertyValue(name="string2", from="year")
		}	
	)	
	private String description;
	
	@Stereotype("MONEY") 
	private BigDecimal amount;

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
}
