package org.openxava.invoicedemo.dashboards;

import java.math.*;

import lombok.*;

// tmr 

@Getter @Setter @AllArgsConstructor 
public class InvoicedPerYear {
	
	int year;
	
	BigDecimal total;
	
	BigDecimal vat;

}
