package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ReceiptTest extends ModuleTestBase {
	
	public ReceiptTest(String testName) {
		super(testName, "Receipt");		
	}

	// TMR ME QUEDÉ POR AQUÍ: EL BUG ESTÁ RESUELTO Y TEST FUNCIONA, FALTA LANZAR LA SUITE
	// TMR   DESPUÉS DE ESTO INVOICEDEMO Y MASTER-DETAIL ARCHETYPES DEBERÍA DE FUNCIONAR.
	public void testCalculationTotalsRecalculatedWhenAddingRowsToElementCollection() throws Exception {
		execute("CRUD.new");
		setValue("year", "2026");
		setValue("number", "99999");
		setValue("customer.number", "1"); // Javi
		
		// Add first detail
		assertCollectionRowCount("details", 0);
		setValueInCollection("details", 0, "product.number", "1"); // MULTAS DE TRAFICO
		setValueInCollection("details", 0, "unitPrice", "19");
		setValueInCollection("details", 0, "quantity", "5");
		assertValueInCollection("details", 0, "amount", "95.00");
		
		// Add second detail
		setValueInCollection("details", 1, "product.number", "2"); // IBM ESERVER ISERIES 270
		setValueInCollection("details", 1, "unitPrice", "19");
		setValueInCollection("details", 1, "quantity", "3");
		assertValueInCollection("details", 1, "amount", "57.00");
		
		assertCollectionRowCount("details", 2);
		
		// Verify totals: sum=152.00, vatPercentage=21%, vat=31.92, total=183.92
		assertTotalInCollection("details", 0, "amount", "152.00");
		assertTotalInCollection("details", 1, "amount", "21");
		assertTotalInCollection("details", 2, "amount", "31.92");
		assertTotalInCollection("details", 3, "amount", "183.92");
		
		// Modify quantity of first detail
		setValueInCollection("details", 0, "quantity", "10");
		assertValueInCollection("details", 0, "amount", "190.00");
		
		// Verify updated totals: sum=247.00, vatPercentage=21%, vat=51.87, total=298.87
		assertTotalInCollection("details", 0, "amount", "247.00");
		assertTotalInCollection("details", 1, "amount", "21");
		assertTotalInCollection("details", 2, "amount", "51.87");
		assertTotalInCollection("details", 3, "amount", "298.87");
	}
	
}
