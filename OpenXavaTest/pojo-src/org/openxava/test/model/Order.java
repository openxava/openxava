package org.openxava.test.model;

import org.openxava.model.*;

import java.math.*;
import java.util.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.*;

@Entity
@Table(name="TOrder")
@View(members=
	"year, number, date;" +
	"customer;" +
	"details;" +
	"amount;" +
	"remarks"
)
public class Order extends Identifiable {
	
	@Column(length=4) 
	@DefaultValueCalculator(CurrentYearCalculator.class)
	@SearchKey 
	private int year;
	
	
	@Column(length=6)
	@SearchKey @ReadOnly 
	private int number;
	
	@Required
	@DefaultValueCalculator(CurrentDateCalculator.class)
	private Date date;	
	
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@ReferenceView("Simplest")
	private Customer customer;
	
	
	@OneToMany(mappedBy="parent", cascade=CascadeType.ALL)	
	@ListProperties("product.number, product.description, quantity, product.unitPrice, amount")
	@RowAction("OrderDetail.reduceQuantity") 
	private Collection<OrderDetail> details = new ArrayList<OrderDetail>();  
	
	@Stereotype("MEMO") 
	private String remarks;
	
	
	@Stereotype("MONEY")
	public BigDecimal getAmount() {
		BigDecimal result = BigDecimal.ZERO;
		for (OrderDetail detail: getDetails()) {
			result = result.add(detail.getAmount());
		}
		return result;
	}
		
	@PrePersist
	private void calculateNumber() throws Exception { 		
		Query query = XPersistence.getManager()
			.createQuery("select max(o.number) from Order o " + 
					"where o.year = :year");
		query.setParameter("year", getYear());	
		Integer lastNumber = (Integer) query.getSingleResult();
		this.setNumber(lastNumber == null?1:lastNumber + 1);
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Collection<OrderDetail> getDetails() {
		return details;
	}

	public void setDetails(Collection<OrderDetail> details) {
		this.details = details;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
		
}
