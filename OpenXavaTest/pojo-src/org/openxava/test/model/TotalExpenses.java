package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.model.*;

/**
 * tmp ME QUEDÉ POR AQUÍ: FALTA CREAR LAS TABLAS Y HACER UNA PRUEBA JUNIT
 * 
 * @author Javier Paniza
 */
public class TotalExpenses extends Identifiable {
	
	@Column(length=40)
	private String description;

	@OneToMany(mappedBy="totalExpenses", cascade=CascadeType.ALL)
	private Collection<ProductExpenses> expenses;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<ProductExpenses> getExpenses() {
		return expenses;
	}

	public void setExpenses(Collection<ProductExpenses> expenses) {
		this.expenses = expenses;
	}
	
}
