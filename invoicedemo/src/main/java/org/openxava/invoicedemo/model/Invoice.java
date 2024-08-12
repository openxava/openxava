package org.openxava.invoicedemo.model;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.*;
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
	
	public static long size() { // tmr		
		return (Long) XPersistence.getManager().createQuery("select count(*) from Invoice").getSingleResult();
	}
	
	public static BigDecimal sumAllTotals() { // tmr
		// tmr ¿Implementar con @Calculation o dejar así?
		// tmr return (BigDecimal) XPersistence.getManager().createQuery("select sum(i.total) from Invoice i").getSingleResult();
		return findAllAsStream()
			.map(Invoice::getTotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	public static Stream<Invoice> findAllAsStream() { // tmr
		return XPersistence.getManager().createQuery("from Invoice i").getResultStream();
	}
	
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
