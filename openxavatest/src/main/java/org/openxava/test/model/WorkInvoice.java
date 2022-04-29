package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;

/**
 *  
 * @author Javier Paniza
 */
@Entity
@View(	
	members="number, description; hours; worker; workCost; tripCost; discount; vatPercentage; total" 
)
public class WorkInvoice { 
	
	@Id
	private int number;
	
	@Column(length=40) @Required
	private String description;
	
	private int hours;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Worker worker;
	
	private BigDecimal tripCost;
	
	private BigDecimal discount;
	
	@ManyToOne
	@DescriptionsList 
	private WorkCost workCost;
	
	@Max(99) 
	@DefaultValueCalculator(
		value=org.openxava.calculators.IntegerCalculator.class,
		properties={ @PropertyValue(name="value", value="16") }		
	)
	private int vatPercentage;
		
	@Calculation("((hours * worker.hourPrice) + tripCost - discount) * (1 + vatPercentage / 100)")
	private BigDecimal total;

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

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	public BigDecimal getTripCost() {
		return tripCost;
	}

	public void setTripCost(BigDecimal tripCost) {
		this.tripCost = tripCost;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public int getVatPercentage() {
		return vatPercentage;
	}

	public void setVatPercentage(int vatPercentage) {
		this.vatPercentage = vatPercentage;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public WorkCost getWorkCost() {
		return workCost;
	}

	public void setWorkCost(WorkCost workCost) {
		this.workCost = workCost;
	}
	
}
