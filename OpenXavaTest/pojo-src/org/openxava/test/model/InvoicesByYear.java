package org.openxava.test.model;

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
	Collection<Invoice> invoices;

}
