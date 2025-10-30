package org.openxava.test.calculators;

import org.openxava.calculators.*;

import lombok.*;
	
@Getter @Setter 	
public class NextNumberForLedgerPeriodCalculator implements ICalculator{
	
	int year; 
	
	public Object calculate() throws Exception { // It does the calculation
		// TMR ME QUEDÉ POR AQUÍ: INTENTANDO REPRODUCIR EL PROBLEMA. ME CARGUÉ LA DB SIN QUERER, HE DE VOLVER A CREAR TABLAS Y LLENAR
		System.out.println("[NextNumberActForYearCalculator.calculate] year=" + year); // tmp
		return year;
		/* tmr
		Query query = XPersistence.getManager() // A JPA query
		.createNativeQuery("select max(i.number) from Livres i" + " where i.annee_oid = :iDoid"); // The query returns
		// the max invoice number of the indicated year
		query.setParameter("iDoid", ann); // We use the injected year as a parameter for the query
		Integer lastNumber = (Integer) query.getSingleResult();
		return lastNumber == null ? 1 : lastNumber + 1; // Returns the last invoice number
		// of the year + 1 or 1 if there is no last number
		 * 
		 */
	}

}