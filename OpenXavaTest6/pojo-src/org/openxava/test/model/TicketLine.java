package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
public class TicketLine extends Identifiable {
	
	@ManyToOne
	private Ticket ticket;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Article article;
	
	private int quantity;
	
	private BigDecimal price;
	
	@Depends("quantity, price")
	public BigDecimal getAmount() {
		return getPrice().multiply(new BigDecimal(getQuantity()));
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	

}
