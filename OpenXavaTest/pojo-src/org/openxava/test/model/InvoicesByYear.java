package org.openxava.test.model;

import java.math.*;
import java.util.*;

import javax.validation.constraints.*;

import org.openxava.annotations.*;
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
		// TMR ME QUEDÉ POR AQUÍ, INTENTANDO REPRODUCIR, ANTES DE HACER LA PRUEBA JUNIT
		// tmr .reduce(BigDecimal.ZERO, BigDecimal::add);
		return new BigDecimal("100");
	}

}
