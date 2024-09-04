package org.openxava.invoicedemo.dashboards;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

@View(members=
	"numberOfInvoices, numberOfCustomers, totalInvoiced; " +
	"invoicingEvolution;" +
	"topCustomers, topYears"
)
public class Dashboard {
	
	@Money
	@LargeDisplay(icon="cash")
	public BigDecimal getTotalInvoiced() { 
		return (BigDecimal) XPersistence.getManager().createQuery("select sum(i.total) from Invoice i").getSingleResult();
	}
	
	@LargeDisplay(icon="animation")
	public long getNumberOfInvoices() { 		
		return count("Invoice"); 
	}
		
	@LargeDisplay(icon="account-group")
	public long getNumberOfCustomers() { 
		return count("Customer");
	}
	
	@Chart
	public Collection<InvoicedPerYear> getInvoicingEvolution() { 
		String jpql = "select new org.openxava.invoicedemo.dashboards.InvoicedPerYear(i.year, sum(i.total), sum(i.vat)) " +
			"from Invoice i " +
			"group by i.year " +
			"order by i.year asc";
		TypedQuery<InvoicedPerYear> query = XPersistence.getManager().createQuery(jpql, InvoicedPerYear.class);
		return query.getResultList();
	}
	
	@SimpleList 
	public Collection<InvoicedPerCustomer> getTopCustomers() { 
		String jpql = "select new org.openxava.invoicedemo.dashboards.InvoicedPerCustomer(i.customer.name, sum(i.total) as amount) " +
			"from Invoice i " +
			"group by i.customer.number, amount " +
			"order by amount desc";
		TypedQuery<InvoicedPerCustomer> query = XPersistence.getManager().createQuery(jpql, InvoicedPerCustomer.class).setMaxResults(5);
		return query.getResultList();
	}
	
	@SimpleList @ListProperties("year, total")
	public Collection<InvoicedPerYear> getTopYears() { 
		String jpql = "select new org.openxava.invoicedemo.dashboards.InvoicedPerYear(i.year, sum(i.total) as amount, sum(i.vat))" +
			"from Invoice i " +
			"group by i.year " +
			"order by amount desc";
		TypedQuery<InvoicedPerYear> query = XPersistence.getManager().createQuery(jpql, InvoicedPerYear.class).setMaxResults(5);
		return query.getResultList();
	}	
	
	private Long count(String entity) {
		return (Long) XPersistence.getManager().createQuery("select count(*) from " + entity).getSingleResult();
	}
	
}
