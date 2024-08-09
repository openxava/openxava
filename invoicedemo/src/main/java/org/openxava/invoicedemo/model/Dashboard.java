package org.openxava.invoicedemo.model;

import java.math.*;

import org.openxava.annotations.*;

/**
 * tmr 
 * @since 7.4
 * @author Javier Paniza
 */
@View(members=
	"totalInvoiced, numberOfInvoices, numberOfCustomers, invoicedPerCustomer; " // tmr + 
	// tmr "turnoverEvolution;" +
	// tmr "turnover, illnessRate, accidentRate, maternityRate;" +  // tmr ¿Dejar esta línea? 
	// tmr "turnoverByYear, moreSeniorWorkers"
)
public class Dashboard {
	
	@Money
	@LargeDisplay(icon="cash")
	public BigDecimal getTotalInvoiced() { // tmr i18n
		// Intead of return an ad hoc number write your own logic
		// with JPA queries and Java to obtain the returned value
		return new BigDecimal("123412.31"); 
	}
	
	@LargeDisplay(icon="cake-variant")
	public int getNumberOfInvoices() { // tmr i18n
		// Intead of return an ad hoc number write your own logic
		// with JPA queries and Java to obtain the returned value		
		return 37; 
	}
		
	@LargeDisplay(icon="account-group")
	public int getNumberOfCustomers() { // tmr i18n
		// Intead of return an ad hoc number write your own logic
		// with JPA queries and Java to obtain the returned value
		return 17; 
	}
	
	@Money
	@LargeDisplay(icon="account-cash")
	public BigDecimal getInvoicedPerCustomer() { // tmr i18n
		// Intead of return an ad hoc number write your own logic
		// with JPA queries and Java to obtain the returned value		
		return new BigDecimal("65.73"); 
	}
			
	/* tmr
	@Chart
	public Collection<StaffTurnover> getTurnoverEvolution() {
		// Here we create some ad hoc elements, but you shoud
		// write your own logic with JPA queries, Java or both
		// to get the list
		Collection<StaffTurnover> result = new ArrayList<>();
		result.add(new StaffTurnover(2020, 9, 5));
		result.add(new StaffTurnover(2021, 12, 3));
		result.add(new StaffTurnover(2022, 14, 1));
		result.add(new StaffTurnover(2023, 4, 16));
		result.add(new StaffTurnover(2024, 3, 21));
		return result;
	}
	*/
	
	/* tmr
	@SimpleList 
	public Collection<StaffTurnover> getTurnoverByYear() {
		// An example that we can see the same data with different format
		return getTurnoverEvolution();
	}	
	*/
	
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
