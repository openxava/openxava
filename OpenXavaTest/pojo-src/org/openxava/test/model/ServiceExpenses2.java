package org.openxava.test.model;

import java.time.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Jeromy Altuna
 */
@Entity
public class ServiceExpenses2 extends Identifiable {
	
	@Column(length=40)
	private String description;
	
	@ElementCollection
	// tmr @ListProperties("invoice.year, invoice.number, invoice.amount, status, receptionist") 
	@ListProperties("invoice.year, invoice.number, invoice.amount, status, receptionist, date[serviceExpenses2.olderDate, serviceExpenses2.newerDate]") // tmr
	private Collection<ServiceExpense2> expenses;
	
	public LocalDate getOlderDate() {
		if (expenses == null || expenses.isEmpty()) return null;
		return expenses.stream().map(ServiceExpense2::getDate).filter(d -> d != null).min(LocalDate::compareTo).get();
	}
	
	public LocalDate getNewerDate() {
		if (expenses == null || expenses.isEmpty()) return null;
		return expenses.stream().map(ServiceExpense2::getDate).filter(d -> d != null).max(LocalDate::compareTo).get();
	}
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<ServiceExpense2> getExpenses() {
		return expenses;
	}

	public void setExpenses(Collection<ServiceExpense2> expenses) {
		this.expenses = expenses;
	}
}
