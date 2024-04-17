package org.openxava.test.tests.bymodule;

/**
 * tmr
 * @author Javier Paniza
 */

public class QuoteWithSumTest extends EmailNotificationsTestBase {
	
	public QuoteWithSumTest(String testName) {
		super(testName, "QuoteWithSum");		
	}
	
	public void testSumInCollectionNotRemovable() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("year", "2014");  
		assertValue("number", "1");  
		
		assertTotalInCollection("details", 0, "amount", "162.00"); 
		assertTotalInCollection("details", 1, "amount",  "21.00"); 
		assertTotalInCollection("details", 2, "amount",  "34.02");
		assertTotalInCollection("details", 3, "amount", "196.02");
		
		assertNoAction("CollectionTotals.removeColumnSum"); // TMR ME QUEDÉ POR AQUÍ. TEST HECHO, FALTA CORREGIR EL BUG
	}
						
}
