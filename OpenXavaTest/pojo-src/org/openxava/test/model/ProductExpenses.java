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
public class ProductExpenses extends Identifiable {
	
	@Column(length=40)
	private String description;
	
	@ElementCollection
	@ListProperties("invoice, product.description, carrier.number, family, subfamily")
	private Collection<ProductExpense> expenses;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<ProductExpense> getExpenses() {
		return expenses;
	}

	public void setExpenses(Collection<ProductExpense> expenses) {
		this.expenses = expenses;
	}
	
}
