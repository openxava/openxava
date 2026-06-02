package org.openxava.test.model;

import java.math.*;
import java.util.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.test.actions.*;

import lombok.*;
import org.openxava.test.filters.ActiveYearFilter;

/**
 *  
 * @author Javier Paniza
 */
@Getter @Setter
@View(members="year; invoices { invoices }, otherInvoices { activeInvoices; activeInvoicesPlusYear } ")
@View(name="ForDialog", members="year; invoices") 
public class InvoicesByYear { 
	
	@Max(2100) @OnChange(OnChangeVoidAction.class)
	int year;
	
	@Condition("${year} = ${this.year}")
	@ListProperties("year, number, date, total[invoicesByYear.totalSum]")
	@NoDefaultActions(forViews="ForDialog") // So the collection is narrow with no data, to test a dialog resize case
	@ListProperties(forViews="ForDialog", value="year, number, date, total, comment")
	Collection<Invoice> invoices;

	@Condition(value="${year} = ?", filter= ActiveYearFilter.class)
	@ListProperties("year, number, date, total")
	Collection<Invoice> activeInvoices;

	@Condition(value="${year} = ${this.year} or ${year} = ?", filter= ActiveYearFilter.class)
	@ListProperties("year, number, date, total")
	Collection<Invoice> activeInvoicesPlusYear;
	
	public BigDecimal getTotalSum() {
		TypedQuery<Invoice> query = XPersistence.getManager().createQuery("SELECT i FROM Invoice i WHERE i.year = :year", Invoice.class);
		query.setParameter("year", year);
		Collection<Invoice> invoices = (Collection<Invoice>) query.getResultList(); 
		return invoices.stream()
			.map(Invoice::getTotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}	

}
