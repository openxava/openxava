package org.openxava.invoicedemo.dashboards;

import java.math.*;

import lombok.*;

@Getter @Setter @AllArgsConstructor 
public class InvoicedPerYear {
	
	int year;
	
	BigDecimal total;
	
	BigDecimal vat;

}
