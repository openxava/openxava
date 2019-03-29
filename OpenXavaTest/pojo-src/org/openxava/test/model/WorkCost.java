package org.openxava.test.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;
import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
@Views({
	// Not default view to test a case
	@View(name="OrderedInvoices", members="description; invoices"),
	@View(name="UnorderedInvoices", members="description; unorderedInvoices")
})
public class WorkCost extends Identifiable {
	
	@Column(length=40) @Required
	private String description;
	
	private int profitPercentage;
	
	@Calculation("sum(invoices.total) * profitPercentage / 100")
	@ReadOnly
	private BigDecimal profit;
	
	@Calculation("sum(invoices.total) + profit")
	@ReadOnly
	private BigDecimal total; // Same name of total from WorkInvoce to test a bug 
	
	@OneToMany (mappedBy="workCost")
	@OrderColumn 
	@ListProperties("number, description, hours, worker.hourPrice, tripCost, discount, vatPercentage, total+[workCost.profitPercentage, workCost.profit, workCost.total]") 
	private List<WorkInvoice> invoices;
	
	
	private int uprofitPercentage; // u prefix because it's for unorderedInvoices
	
	@Calculation("sum(unorderedInvoices.total) * uprofitPercentage / 100")
	@ReadOnly
	private BigDecimal uprofit; // u prefix because it's for unorderedInvoices
	
	@Calculation("sum(unorderedInvoices.total) + uprofit")
	@ReadOnly
	private BigDecimal utotal; // u prefix because it's for unorderedInvoices 
	
	@OneToMany (mappedBy="workCost")
	@ListProperties("number, description, hours, worker.hourPrice, tripCost, discount, vatPercentage, total+[workCost.uprofitPercentage, workCost.uprofit, workCost.utotal]") 
	private Collection<WorkInvoice> unorderedInvoices;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<WorkInvoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<WorkInvoice> invoices) {
		this.invoices = invoices;
	}	

	public int getProfitPercentage() {
		return profitPercentage;
	}

	public void setProfitPercentage(int profitPercentage) {
		this.profitPercentage = profitPercentage;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	
	public Collection<WorkInvoice> getUnorderedInvoices() {
		return unorderedInvoices;
	}

	public void setUnorderedInvoices(Collection<WorkInvoice> unorderedInvoices) {
		this.unorderedInvoices = unorderedInvoices;
	}

	public int getUprofitPercentage() {
		return uprofitPercentage;
	}

	public void setUprofitPercentage(int uprofitPercentage) {
		this.uprofitPercentage = uprofitPercentage;
	}

	public BigDecimal getUprofit() {
		return uprofit;
	}

	public void setUprofit(BigDecimal uprofit) {
		this.uprofit = uprofit;
	}

	public BigDecimal getUtotal() {
		return utotal;
	}

	public void setUtotal(BigDecimal utotal) {
		this.utotal = utotal;
	}
	
}
