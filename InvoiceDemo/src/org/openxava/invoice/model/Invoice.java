package org.openxava.invoice.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.model.*;

import lombok.*;

@Entity @Getter @Setter
@View(members=
	"year, number, date;" +
	"customer;" +
	"details { details };" +
	"remarks { remarks }"
)
@Tab(properties="year, number, date, customer.name, remarks")
public class Invoice extends Identifiable {
	
	@DefaultValueCalculator(CurrentYearCalculator.class)
	@Column(length=4) @Required
	int year;
	
	@Column(length=6) @Required
	int number;
	
	@Required @DefaultValueCalculator(CurrentDateCalculator.class) 
	Date date;
	
	@Column(length=2) @Required
	@DefaultValueCalculator(value=IntegerCalculator.class, properties=@PropertyValue(name="value", value="21"))
	int vatPercentage;
	
	@ReferenceView("Simple") 
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	Customer customer;
	
	@ElementCollection @OrderColumn
	@ListProperties("product.number, product.description, unitPrice, quantity, amount[invoice.sum, invoice.vatPercentage, invoice.vat, invoice.total]")
	List<InvoiceDetail> details;
	
	@Stereotype("HTML_TEXT")
	String remarks;
	
	public BigDecimal getSum() {
		BigDecimal sum = BigDecimal.ZERO;
		for (InvoiceDetail detail: details) {
			sum = sum.add(detail.getAmount());
		}
		return sum;
	}
	
	@Depends("sum, vatPercentage")
	public BigDecimal getVat() {
		return getSum().multiply(new BigDecimal(getVatPercentage()).divide(new BigDecimal(100))).setScale(2, RoundingMode.UP);
	}
	
	@Depends("sum, vat")
	public BigDecimal getTotal() {
		return getSum().add(getVat()).setScale(2, RoundingMode.UP);
	}
	
}
