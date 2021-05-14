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
@View(name="OrderAsEmbedded")
public class OrderIssue extends Identifiable {
	
	private Date date;
	
	@Stereotype("MEMO")
	private String description;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@SearchKey 
	@AsEmbedded(forViews="OrderAsEmbedded") // tmp
	private Order order;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

}
