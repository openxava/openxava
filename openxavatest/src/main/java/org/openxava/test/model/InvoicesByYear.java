package org.openxava.test.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.test.actions.*;

import lombok.*;

/**
 *  
 * @author Javier Paniza
 */
@Getter @Setter
@View(members="year; invoices { invoices } ") 
public class InvoicesByYear { 
	
	@Max(2100) @OnChange(OnChangeVoidAction.class)
	int year;
	
	@Condition("${year} = ${this.year}")
	@ListProperties("year, number, date, total[invoicesByYear.totalSum]") 
	Collection<Invoice> invoices;
	
	public BigDecimal getTotalSum() {
		Query query = XPersistence.getManager().createQuery("from Invoice i where i.year = :year");
		query.setParameter("year", year);
		Collection<Invoice> invoices = query.getResultList(); 
		return invoices.stream()
			.map(Invoice::getTotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}	

}
