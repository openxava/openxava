package org.openxava.invoicedemo.dashboards;

import java.math.*;

import lombok.*;

@Getter @Setter @AllArgsConstructor 
public class InvoicedPerCustomer {
	
	String customer;
	
	BigDecimal amount;
	
}
