package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
public class ProductExpenses2 extends Identifiable {
	
	@Column(length=40)
	private String description;
	
	@ElementCollection
	@ListProperties("carrier.number, invoice, product.description") 
	private Collection<ProductExpense2> expenses;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<ProductExpense2> getExpenses() {
		return expenses;
	}

	public void setExpenses(Collection<ProductExpense2> expenses) {
		this.expenses = expenses;
	}
	
}
