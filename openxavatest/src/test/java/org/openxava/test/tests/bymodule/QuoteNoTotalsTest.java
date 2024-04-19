package org.openxava.test.tests.bymodule;

/**
 * tmr
 * @author Javier Paniza
 */

public class QuoteNoTotalsTest extends EmailNotificationsTestBase {
	
	public QuoteNoTotalsTest(String testName) {
		super(testName, "QuoteNoTotals");		
	}
	
	public void testSumInCollectionNotRemovable() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("year", "2014");  
		assertValue("number", "1"); 
		assertTotalInCollection("details", 0, "amount", "");
		execute("CollectionTotals.sumColumn", "property=amount,collection=details");
		assertTotalInCollection("details", 0, "amount", "162.00");
		execute("CollectionTotals.removeColumnSum", "property=amount,collection=details");
		assertTotalInCollection("details", 0, "amount", "");
		
		changeModule("QuoteWithSum");
		execute("List.viewDetail", "row=0");
		assertValue("year", "2014");  
		assertValue("number", "1");  
		
		assertTotalInCollection("details", 0, "amount", "162.00"); 
		assertTotalInCollection("details", 1, "amount",  "21.00"); 
		assertTotalInCollection("details", 2, "amount",  "34.02");
		assertTotalInCollection("details", 3, "amount", "196.02");
		
		assertNoAction("CollectionTotals.removeColumnSum"); 
	}
						
}
