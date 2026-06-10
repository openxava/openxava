package org.openxava.test.model;

import java.math.*;
import java.time.*;
import java.util.*;

import jakarta.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.model.*;

import lombok.*;

// WARNING: DON'T CHANGE THE CODE OF THIS CLASS because it reproduces a bug
//    with resizing the browser not reproducible in any other very similar cases,
//    and we don't know the exact cause, although it's related to the collection
//    with summation and totals, even when the problem arises in list mode.
@Entity @Getter @Setter
@View(members=
	"year, number, date;" +
	"customer;" +
	"details { details };" +
	"remarks { remarks }"
)
@Tab(properties="year, number, date, customer.name, total, remarks")
public class Receipt extends Identifiable {
		
	@DefaultValueCalculator(CurrentYearCalculator.class)
	@Column(length=4) @Required
	int year;
	
	@Column(length=6) @Required
	int number;
	
	@Required @DefaultValueCalculator(CurrentLocalDateCalculator.class) 
	LocalDate date;
	
	@Column(length=2) @Required
	@DefaultValueCalculator(value=IntegerCalculator.class, properties=@PropertyValue(name="value", value="21"))
	int vatPercentage;
	
	@ReferenceView("Simple") 
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	Customer customer;
	
	@ElementCollection
	@ListProperties("product.number, product.description, unitPrice, quantity, amount+[receipt.vatPercentage, receipt.vat, receipt.total]")
	List<ReceiptDetail> details;
	
	@HtmlText
	String remarks;

	@ReadOnly @Money
	@Calculation("sum(details.amount) * vatPercentage / 100")
	BigDecimal vat;

	@ReadOnly @Money
	@Calculation("sum(details.amount) + vat")
	BigDecimal total;

}
