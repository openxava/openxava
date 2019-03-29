package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Jeromy Altuna
 */
public class HunterTest extends ModuleTestBase {
	
	public HunterTest(String testName){
		super(testName, "Hunter");
	}
	
	public void testLabelInCollection() throws Exception{
		execute("CRUD.new");
		assertLabelInCollection("hounds", 2, "Its birth");
	}
	
	public void testHunterWithAtLeastOneHound() throws Exception {
		assertListRowCount(1);
		execute("List.viewDetail", "row=0");
		assertValue("name", "DE ALFORJA");
		assertCollectionRowCount("hounds", 1);
		execute("Collection.removeSelected", 
			"row=0,viewObject=xava_view_section0_hounds");
		assertError("It's required at least 1 element in Hounds of Hunter");
		assertValueInCollection("hounds", 0, "name", "LEBRERO");		
	}
	
	public void testAddMaximumTwoHounds() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("name", "DE ALFORJA");
		execute("Collection.add", "viewObject=xava_view_section0_hounds");
		checkAll();
		execute("AddToCollection.add");
		assertError("More than 2 items in Hounds of Hunter are not allowed");
		uncheckAll();		
		execute("AddToCollection.add", "row=1");
		assertMessage("1 element(s) added to Hounds of Hunter");
		execute("Collection.add", "viewObject=xava_view_section0_hounds");
		execute("AddToCollection.add", "row=3");
		assertError("More than 2 items in Hounds of Hunter are not allowed");
		execute("AddToCollection.cancel");
		assertCollectionRowCount("hounds", 2);
		
		execute("Collection.removeSelected", 
			"row=1,viewObject=xava_view_section0_hounds");
		assertNoErrors();
	}
	
	public void testFilterEmptyValuesInCollection() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("Collection.add", "viewObject=xava_view_section0_hounds");
		execute("AddToCollection.add", "row=1");
		assertCollectionRowCount("hounds", 2);
		
		assertFalse(isNotVisibleConditionValue(0));
		assertFalse(isNotVisibleConditionValue(2));
		
		// Filter String
		setConditionComparators("hounds", new String [] {"empty_comparator"});		
		// execute("List.filter", "collection=hounds");
		assertCollectionRowCount("hounds", 0); 
		assertTrue(isNotVisibleConditionValue(0));
		
		// Filter Date
		setConditionComparators("hounds", new String [] {"=", "=", "empty_comparator"});
		// execute("List.filter", "collection=hounds");
		assertCollectionRowCount("hounds", 1);
		assertTrue(isNotVisibleConditionValue(2));
		assertFalse(isNotVisibleConditionValue(0));
		
		setConditionComparators("hounds", new String [] {"=", "=", "="});
		execute("List.filter", "collection=hounds");
		execute("Collection.removeSelected", 
			"row=1,viewObject=xava_view_section0_hounds");
		assertNoErrors();
	}
	
	public void testFilterNotEmptyValuesInCollection() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("Collection.add", "viewObject=xava_view_section0_hounds");
		execute("AddToCollection.add", "row=1");
		assertCollectionRowCount("hounds", 2); 
		
		assertFalse(isNotVisibleConditionValue(0));
		assertFalse(isNotVisibleConditionValue(2));
		
		// Filter String
		setConditionComparators("hounds", new String [] {"not_empty_comparator"});		
		// execute("List.filter", "collection=hounds");
		assertCollectionRowCount("hounds", 2);
		assertTrue(isNotVisibleConditionValue(0));
		
		// Filter Date
		setConditionComparators("hounds", new String [] {"=", "=", "not_empty_comparator"});
		// execute("List.filter", "collection=hounds");
		assertCollectionRowCount("hounds", 1);
		assertTrue(isNotVisibleConditionValue(2));
		assertFalse(isNotVisibleConditionValue(0));
		
		setConditionComparators("hounds", new String [] {"=", "=", "="});
		execute("List.filter", "collection=hounds");
		execute("Collection.removeSelected", 
			"row=1,viewObject=xava_view_section0_hounds");
		assertNoErrors();
	}
	
	public void testAddUntrainedHound_validationJPACallback() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("Collection.add", "viewObject=xava_view_section0_hounds");
		execute("AddToCollection.add", "row=2");
		assertErrorsCount(2); 
		assertError("Untrained OTTERHOUND, less than 2 years old");
		execute("AddToCollection.cancel");
		assertCollectionRowCount("hounds", 1);
	}
	
	private boolean isNotVisibleConditionValue(int index) {
		String idConditionValue = 
				"ox_" + getXavaJUnitProperty("application") + 
				"_Hunter__xava_collectionTab_hounds_conditionValue___" + index;
		HtmlElement input = getHtmlPage().getHtmlElementById(idConditionValue); 
		return input.getAttribute("style").contains("display: none");			
	}
}
