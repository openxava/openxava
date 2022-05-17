package org.openxava.test.model;

import java.sql.*;
import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Embeddable
public class ServiceExpense {
	
	@ManyToOne(fetch=FetchType.LAZY)
	private ServiceInvoice invoice;
	
	@Editor("DateTimeSeparatedCalendar")
	private Timestamp time;
	
	public enum Status { PAID, PENDING, REJECTED }
	private Status status;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	private Receptionist receptionist; 
	
	public ServiceInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(ServiceInvoice invoice) {
		this.invoice = invoice;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Receptionist getReceptionist() {
		return receptionist;
	}

	public void setReceptionist(Receptionist receptionist) {
		this.receptionist = receptionist;
	}

}
