package org.openxava.test.model;

import java.time.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * tmp 
 * TMP ME QUEDÉ POR AQUÍ: MODELO CREADO, TABLAS CREADAS. FALTA REPRODUCIR EL ERROR. 
 * 
 * @author Javier Paniza
 */

@Entity
public class AccountingDocument extends Identifiable {
	
	@Required
	@Column(length = 4)
	private int number;
	
	@Required
	private LocalDate date;
	
	@Required
	@Column(length = 40)
	private String description;
	
	@OneToMany(mappedBy="document",  cascade=CascadeType.ALL, orphanRemoval=true)
	private Collection<AccountingDocumentDetail> positions;

	public Collection<AccountingDocumentDetail> getPositions() {
		return positions;
	}

	public void setPositions(Collection<AccountingDocumentDetail> positions) {
		this.positions = positions;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}
