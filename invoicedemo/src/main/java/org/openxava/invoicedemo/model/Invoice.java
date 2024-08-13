package org.openxava.invoicedemo.model;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.*;
import org.openxava.model.*;

import lombok.*;

@Entity @Getter @Setter
@View(members=
	"year, number, date;" +
	"customer;" +
	"details { details };" +
	"remarks { remarks }"
)
@Tab(properties="year, number, date, customer.name, remarks")
public class Invoice extends Identifiable {
	
	public static long size() { // tmr		
		return (Long) XPersistence.getManager().createQuery("select count(*) from Invoice").getSingleResult();
	}
	
	public static BigDecimal sumAllTotals() { // tmr
		// tmr ¿Implementar con @Calculation o dejar así?
		return (BigDecimal) XPersistence.getManager().createQuery("select sum(i.total) from Invoice i").getSingleResult();
		/* Cronometré y era igual con 3 facturas, cronometrar con más
		return findAllAsStream()
			.map(Invoice::getTotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		*/	
	}
	
	public static Collection<InvoicedPerYear> invoicedPerYear() {
		/* tmr
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
        */
		// tmr ini
		String jpql = "select new org.openxava.invoicedemo.model.InvoicedPerYear(i.year, sum(i.total)) " +
			"from Invoice i " +
			"group by i.year " +
			"order by i.year asc";
		TypedQuery<InvoicedPerYear> query = XPersistence.getManager().createQuery(jpql, InvoicedPerYear.class);
		return query.getResultList();		
		// tmr fin
	}
	
	public static Stream<Invoice> findAllAsStream() { // tmr
		return XPersistence.getManager().createQuery("from Invoice i").getResultStream();
	}
	
	@DefaultValueCalculator(CurrentYearCalculator.class)
	@Column(length=4) @Required
	int year;
	
	@Column(length=6) @Required
	int number;
	
	@Required @DefaultValueCalculator(CurrentDateCalculator.class) 
	Date date;
	
	@Column(length=2) @Required
	@DefaultValueCalculator(value=IntegerCalculator.class, properties=@PropertyValue(name="value", value="21"))
	int vatPercentage;
	
	@ReferenceView("Simple") 
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	Customer customer;
	
	@ElementCollection @OrderColumn
	// tmr @ListProperties("product.number, product.description, unitPrice, quantity, amount[invoice.sum, invoice.vatPercentage, invoice.vat, invoice.total]")
	@ListProperties("product.number, product.description, unitPrice, quantity, amount+[invoice.vatPercentage, invoice.vat, invoice.total]")
	List<InvoiceDetail> details;
	
	@Stereotype("HTML_TEXT")
	String remarks;

	/* tmr
	public BigDecimal getSum() {
		BigDecimal sum = BigDecimal.ZERO;
		for (InvoiceDetail detail: details) {
			sum = sum.add(detail.getAmount());
		}
		return sum;
	}
	
	@Depends("sum, vatPercentage")
	public BigDecimal getVat() {
		return getSum().multiply(new BigDecimal(getVatPercentage()).divide(new BigDecimal(100))).setScale(2, RoundingMode.UP);
	}
	
	@Depends("sum, vat")
	public BigDecimal getTotal() {
		return getSum().add(getVat()).setScale(2, RoundingMode.UP);
	}
	*/
	// tmr ini
	@ReadOnly @Money
	@Calculation("sum(details.amount) * vatPercentage / 100")
	BigDecimal vat;
	
	@ReadOnly @Money
	@Calculation("sum(details.amount) + vat")
	BigDecimal total;
	// tmr fin
	
}
