package org.openxava.chatvoice.dashboards;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

@View(members=
	"numberOfInvoices, numberOfCustomers, totalSum; " +
	"evolution;" +
	"topCustomers, topYears"
)
public class Dashboard {
	
	@Money
	@LargeDisplay(icon="cash")
	public BigDecimal getTotalSum() { 
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
	public Collection<PerYear> getEvolution() { 
		String jpql = "select new org.openxava.chatvoice.dashboards.PerYear(i.year, sum(i.total), sum(i.tax)) " +
			"from Invoice i " +
			"group by i.year " +
			"order by i.year asc";
		TypedQuery<PerYear> query = XPersistence.getManager().createQuery(jpql, PerYear.class);
		return query.getResultList();
	}
	
	@SimpleList 
	public Collection<PerCustomer> getTopCustomers() { 
		String jpql = "select new org.openxava.chatvoice.dashboards.PerCustomer(i.customer.name, sum(i.total) as amount) " +
			"from Invoice i " +
			"group by i.customer.number, amount " +
			"order by amount desc";
		TypedQuery<PerCustomer> query = XPersistence.getManager().createQuery(jpql, PerCustomer.class).setMaxResults(5);
		return query.getResultList();
	}
	
	@SimpleList @ListProperties("year, total")
	public Collection<PerYear> getTopYears() { 
		String jpql = "select new org.openxava.chatvoice.dashboards.PerYear(i.year, sum(i.total) as amount, sum(i.tax)) " +
			"from Invoice i " +
			"group by i.year " +
			"order by amount desc";
		TypedQuery<PerYear> query = XPersistence.getManager().createQuery(jpql, PerYear.class).setMaxResults(5);
		return query.getResultList();
	}	
	
	private Long count(String entity) {
		return (Long) XPersistence.getManager().createQuery("select count(*) from " + entity).getSingleResult();
	}
	
}
