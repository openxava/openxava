package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
public class Ticket {
	
	@Id
	private int number;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="ticket")
	private Collection<TicketLine> lines;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Collection<TicketLine> getLines() {
		return lines;
	}

	public void setLines(Collection<TicketLine> lines) {
		this.lines = lines;
	}

}
