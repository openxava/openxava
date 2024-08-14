package org.openxava.invoicedemo.dashboards;

import java.math.*;

import lombok.*;

// tmr 

@Getter @Setter @AllArgsConstructor 
public class InvoicedPerCustomer {
	
	String customer;
	
	BigDecimal amount;
	
}
