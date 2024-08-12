package org.openxava.invoicedemo.model;

import java.math.*;

import lombok.*;

// tmr 

@Getter @Setter
@AllArgsConstructor // tmr Quitar
public class InvoicedPerYear {
	
	int year;
	
	BigDecimal amount;

}
