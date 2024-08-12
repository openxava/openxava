package org.openxava.invoicedemo.model;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import org.openxava.annotations.*;

/**
 * tmr 
 * @since 7.4
 * @author Javier Paniza
 */
@View(members=
	"totalInvoiced, numberOfInvoices, numberOfCustomers, invoicedPerCustomer; " + 
	"invoicingEvolution;" // tmr +
	// tmr "turnover, illnessRate, accidentRate, maternityRate;" +  // tmr ¿Dejar esta línea? 
	// tmr "turnoverByYear, moreSeniorWorkers"
)
public class Dashboard {
	
	@Money
	@LargeDisplay(icon="cash")
	public BigDecimal getTotalInvoiced() { // tmr i18n
		return Invoice.sumAllTotals(); 
	}
	
	@LargeDisplay(icon="animation")
	public long getNumberOfInvoices() { // tmr i18n		
		return Invoice.size(); 
	}
		
	@LargeDisplay(icon="account-group")
	public long getNumberOfCustomers() { // tmr i18n
		return Customer.size(); 
	}
	
	@Money
	@LargeDisplay(icon="account-cash")
	public BigDecimal getInvoicedPerCustomer() { // tmr i18n
		return getTotalInvoiced().divide(new BigDecimal(getNumberOfCustomers()), 2, BigDecimal.ROUND_HALF_DOWN); 
	}
			
	@Chart
	public Collection<InvoicedPerYear> getInvoicingEvolution() {
		// tmr Así o con JPA
		// TMR ME QUEDÉ POR AQUÍ: ESTO YA FUNCIONA, TENGO QUE DETERMINAR:
		// TMR  - SI DEBERÍA IMPLEMENTARLO CON JPA Y 
		// TMR  - SI DEBERÍA MOVER LA LÓGICA A Invoice 
        Map<Integer, BigDecimal> invoicingPerYear = Invoice.findAllAsStream()
            .collect(Collectors.groupingBy(
                Invoice::getYear,
                TreeMap::new,
                Collectors.reducing(BigDecimal.ZERO, Invoice::getTotal, BigDecimal::add)
            )
        );

        Collection<InvoicedPerYear> result = new ArrayList<>();
        invoicingPerYear.forEach((year, total) -> result.add(new InvoicedPerYear(year, total)));

        return result;
	}
	
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
