package org.openxava.test.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
//@View(name = "DateTime", members="expenses2")
//@View(members= "description; expenses")
//@Tab(properties = "description, discount")
public class ServiceExpenses extends Identifiable {
	
	@Column(length=40)
	private String description;
	
	@Calculation("sum(expenses.invoice.amount) * 0.15")
	@ReadOnly
	@Stereotype("MONEY") // MONEY to test a case
	private BigDecimal discount;
	
	@ElementCollection
	@ListProperties("invoice.year, invoice.number, invoice.amount+[serviceExpenses.discount], status, receptionist")   
	private Collection<ServiceExpense> expenses;

//	@Embedded
//	@Getter @Setter
//	ServiceExpense expenses2;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<ServiceExpense> getExpenses() {
		return expenses;
	}

	public void setExpenses(Collection<ServiceExpense> expenses) {
		this.expenses = expenses;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	
}
