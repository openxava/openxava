package org.openxava.invoicedemo.dashboards;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

/**
 * tmr
 * TMR NO FUNCIONA AL PONERLO EN dashboards. INTENTANDO QUE OPENXAVA LO COJA, SINO OTRA OPCIÓN ES PONERLO
 * TMR EN MODEL CON LAS CLASES ANIDADAS 
 * @since 7.4
 * @author Javier Paniza
 */
@View(members=
	"totalInvoiced, numberOfInvoices, numberOfCustomers, invoicedPerCustomer; " + 
	"invoicingEvolution;" +
	// tmr "turnover, illnessRate, accidentRate, maternityRate;" +  // tmr ¿Dejar esta línea? 
	// tmr "turnoverByYear, moreSeniorWorkers"
	"betterCustomers"
)
public class Dashboard {
	
	@Money
	@LargeDisplay(icon="cash")
	public BigDecimal getTotalInvoiced() { // tmr i18n
		return (BigDecimal) XPersistence.getManager().createQuery("select sum(i.total) from Invoice i").getSingleResult(); 
	}
	
	@LargeDisplay(icon="animation")
	public long getNumberOfInvoices() { // tmr i18n		
		return count("Invoice"); 
	}
		
	@LargeDisplay(icon="account-group")
	public long getNumberOfCustomers() { // tmr i18n
		return count("Customer");
	}
	
	@Money
	@LargeDisplay(icon="account-cash")
	public BigDecimal getInvoicedPerCustomer() { // tmr i18n
		return getTotalInvoiced().divide(new BigDecimal(getNumberOfCustomers()), 2, BigDecimal.ROUND_HALF_DOWN); 
	}
			
	@Chart
	public Collection<InvoicedPerYear> getInvoicingEvolution() {
		String jpql = "select new org.openxava.invoicedemo.model.InvoicedPerYear(i.year, sum(i.total), sum(i.vat)) " +
			"from Invoice i " +
			"group by i.year " +
			"order by i.year asc";
		TypedQuery<InvoicedPerYear> query = XPersistence.getManager().createQuery(jpql, InvoicedPerYear.class);
		return query.getResultList();
	}
	
	@SimpleList 
	public Collection<InvoicedPerCustomer> getBetterCustomers() {
		String jpql = "select new org.openxava.invoicedemo.model.InvoicedPerCustomer(i.customer.name, sum(i.total) as amount) " +
			"from Invoice i " +
			"group by i.customer.number, amount " +
			"order by amount desc";
		TypedQuery<InvoicedPerCustomer> query = XPersistence.getManager().createQuery(jpql, InvoicedPerCustomer.class);
		return query.getResultList();
	}
	
	private Long count(String entity) {
		return (Long) XPersistence.getManager().createQuery("select count(*) from " + entity).getSingleResult();
	}

	
	/* tmr
	@ReadOnly // Not needed @SimpleList are read only by default
	@ViewAction("") // Not needed @SimpleList has not actions by default
	@SimpleList 
	public Collection<Worker> getMoreSeniorWorkers() {
		// An example using JPA, note the setMaxResults(5) to limit the size
		return XPersistence.getManager().createQuery("from Worker").setMaxResults(5).getResultList();
	}
	*/
	
}
