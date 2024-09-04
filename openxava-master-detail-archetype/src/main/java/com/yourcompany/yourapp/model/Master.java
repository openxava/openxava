package com.yourcompany.yourapp.model;

import java.math.*;
import java.time.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.model.*;

import lombok.*;

// RENAME THIS CLASS AS Invoice, PurchaseOrder, WorkOrder, Delivery, Account, Shipment, etc.

// YOU CAN RENAME THE MEMBERS BELOW AT YOUR CONVENIENCE, 
// FOR EXAMPLE Person person BY Customer customer,
// BUT CHANGE ALL REFERENCES IN ALL CODE USING SEARCH AND REPLACE FOR THE PROJECT. 
// DON'T USE REFACTOR > RENAME FOR MEMBERS BECAUSE IT DOESN'T CHANGE THE ANNOTATIONS CONTENT.

@Entity @Getter @Setter
@View(members=
	"year, number, date;" +
	"person;" +
	"details { details };" +
	"remarks { remarks }"
)
@Tab(properties="year, number, date, person.name, total, remarks")
public class Master extends Identifiable {
	
	@DefaultValueCalculator(CurrentYearCalculator.class)
	@Column(length=4) @Required
	int year;
	
	@Column(length=6) @Required
	int number;
	
	@Required @DefaultValueCalculator(CurrentLocalDateCalculator.class) 
	LocalDate date;
	
	@Column(length=2) @Required
	@DefaultValueCalculator(value=IntegerCalculator.class, properties=@PropertyValue(name="value", value="21"))
	int taxPercentage;
	
	@ReferenceView("Simple") 
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	Person person;
	
	@ElementCollection @OrderColumn
	@ListProperties("item.number, item.description, unitPrice, quantity, amount+[master.taxPercentage, master.tax, master.total]") 
	List<Detail> details;
	
	@HtmlText
	String remarks;
	
	@ReadOnly @Money
	@Calculation("sum(details.amount) * taxPercentage / 100")
	BigDecimal tax;
	
	@ReadOnly @Money
	@Calculation("sum(details.amount) + tax")
	BigDecimal total;	
	
}
