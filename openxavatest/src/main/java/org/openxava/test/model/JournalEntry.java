package org.openxava.test.model;

import org.openxava.annotations.*;
import org.openxava.model.*;
import org.openxava.test.validators.*;

import java.math.*;

import javax.persistence.*;

/**
 *
 * @author Javier Paniza
 */

@Entity
@EntityValidator(value=
	JournalEntryValidator.class, 
	properties=@PropertyValue(name="theJournal")
) 
public class JournalEntry extends Identifiable {

	@ManyToOne 
	private Journal theJournal;
	
	@Column(length=40) @Required
	private String description;
	
	@Required
	private BigDecimal cantidad;

	public Journal getTheJournal() {
		return theJournal;
	}

	public void setTheJournal(Journal theJournal) {
		this.theJournal = theJournal;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}
	
}
