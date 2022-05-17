/**
 * 
 */
package org.openxava.test.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.openxava.annotations.*;

/**
 * Class person as described in bug 3047205
 * @author Federico Alcantara
 *
 */
@Entity
@View(name="PersonOnlyNames")
@Tab(properties = "repEmployeeNumber, person.personFirstName, person.personLastName, " +
		"person.phoneNumber.phoneDigits")
public class SalesRepresentative implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	private int repEmployeeNumber;
	private BigDecimal repCommissionRate;
	
	@ReferenceView(forViews="PersonOnlyNames", value="OnlyNames") 
	private Person person; // This is different in order to provoke an early exception, related to field name equal (ignoring case) to Class.
	
	/**
	 * @return the repEmployeeNumber
	 */
	public int getRepEmployeeNumber() {
		return repEmployeeNumber;
	}
	/**
	 * @param repEmployeeNumber the repEmployeeNumber to set
	 */
	public void setRepEmployeeNumber(int repEmployeeNumber) {
		this.repEmployeeNumber = repEmployeeNumber;
	}
	/**
	 * @return the repCommissionRate
	 */
	public BigDecimal getRepCommissionRate() {
		return repCommissionRate;
	}
	/**
	 * @param repCommissionRate the repCommissionRate to set
	 */
	public void setRepCommissionRate(BigDecimal repCommissionRate) {
		this.repCommissionRate = repCommissionRate;
	}
	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}
	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

}
