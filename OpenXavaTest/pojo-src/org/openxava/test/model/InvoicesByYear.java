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
	@ListProperties("year, number, date, total[invoicesByYear.totalSum]") // tmr
	Collection<Invoice> invoices;
	
	public BigDecimal getTotalSum() {
		System.out.println("[InvoicesByYear.getTotalSum] year=" + year); // tmp
		Query query = XPersistence.getManager().createQuery("from Invoice i where i.year = :year");
		query.setParameter("year", year);
		Collection<Invoice> invoices = query.getResultList(); 
		BigDecimal result = invoices.stream()
			.map(Invoice::getTotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		System.out.println("[InvoicesByYear.getTotalSum] result=" + result); // tmp
		return result;
	}

	@Override
	public String toString() { // tmr
		return "InvoicesByYear [year=" + year + "]";
	}
	
	

}
