package org.openxava.test.tests;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;
import org.openxava.util.*;


/**
 * @author Javier Paniza
 */

public class DeliveryTypeTest extends ModuleTestBase {
	
	public DeliveryTypeTest(String testName) {
		super(testName, "DeliveryType");		
	}
	
	public void testParseObjectWithMultipleKeyThatAreReferenceInStereotypes() throws Exception {
		execute("CRUD.new");
		Delivery delivery = findDelivery();
		setValue("comboDeliveries", toKeyString(delivery));
		execute("DeliveryType.assertComboDeliveries");
		assertNoErrors();
		assertMessage("comboDeliveries=" + toKeyString(delivery)); 
		assertValue("comboDeliveries", toKeyString(delivery));
	}	
			
	public void testSaveActionNotResetWithAndWithoutRefresh_assertIconAndImage_actionInCorrectBar() throws Exception {
		execute("CRUD.new");
		assertIconAndImage(); 
		actionInCorrectBar(); 
		setValue("number", "66");
		setValue("description", "JUNIT &%=+"); // &%=+ is to test URL special characters
		execute("DeliveryType.saveNotReset");
		assertNoErrors();
		
		assertValue("number", "66");		
		assertValue("description", "JUNIT &%=+ CREATED"); // 'CREATED' is added in postcreate
		assertNoEditable("number");
		assertEditable("description");
		
		setValue("description", "JUNIT M CREATED"); // We modify because if it is not modified it is not saved hence calculators are not executed
		execute("DeliveryType.saveNotReset");
		assertValue("number", "66");		
		assertValue("description", "JUNIT M CREATED MODIFIED"); // 'MODIFIED' is added in postcreate
		assertNoEditable("number");
		assertEditable("description");
		
		
		execute("CRUD.delete");		
		assertNoErrors();
		assertMessage("Delivery type deleted successfully");
		
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "JUNIT"); 
		execute("DeliveryType.saveNotRefresh");
		assertNoErrors();
		
		assertValue("number", "66");		
		assertValue("description", "JUNIT"); // 'CREATED' though added in database is not displayed 
		assertNoEditable("number");
		assertEditable("description");
		
		setValue("description", "JUNIT M CREATED"); // We modify it
		execute("DeliveryType.saveNotRefresh");
		assertValue("number", "66");		
		assertValue("description", "JUNIT M CREATED"); // 'MODIFIED' though added in database is not displayed
		assertNoEditable("number");
		assertEditable("description");
		
		execute("CRUD.delete");		
		assertNoErrors();
		assertMessage("Delivery type deleted successfully");				 						
	}
	
	public void testSaveNotResetOnCreateOnModify() throws Exception {  
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "JUNIT"); 
		execute("DeliveryType.saveResetOnCreateNotRefreshAfter");
		assertNoErrors();
		assertMessage("Delivery type created successfully");
		assertValue("number", "");
		assertValue("description", "");
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("description", "JUNIT CREATED");
		setValue("description", "JUNIT");
		execute("DeliveryType.saveResetOnCreateNotRefreshAfter");
		assertNoErrors();
		assertMessage("Delivery type modified successfully");
		assertValue("number", "66");
		assertValue("description", "JUNIT");
		
		execute("DeliveryType.saveResetOnCreate");
		assertNoErrors();
		assertMessage("Delivery type modified successfully");
		assertValue("number", "66");
		assertValue("description", "JUNIT MODIFIED");
		
		setValue("description", "JUNIT 2");
		execute("DeliveryType.saveResetOnModify");
		assertNoErrors();
		assertMessage("Delivery type modified successfully");
		assertValue("number", "");
		assertValue("description", "");
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("description", "JUNIT 2 MODIFIED");
		
		execute("CRUD.delete");		
		assertNoErrors();
		assertMessage("Delivery type deleted successfully");
		
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "JUNIT");
		execute("DeliveryType.saveResetOnModify");
		assertNoErrors();
		assertMessage("Delivery type created successfully");
		assertValue("number", "66");
		assertValue("description", "JUNIT CREATED");
		
		execute("CRUD.delete");		
		assertNoErrors();
		assertMessage("Delivery type deleted successfully");
	}
	
	private void assertIconAndImage() { 
		String saveLinkXml = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_DeliveryType__CRUD___save").asXml();
		assertTrue(saveLinkXml.contains("<i class="));
		assertFalse(saveLinkXml.contains("images/save.gif"));
		
		String saveNotResetLinkXml = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_DeliveryType__DeliveryType___saveNotReset").asXml();
		assertTrue(saveNotResetLinkXml.contains("images/save.gif"));
		assertFalse(saveNotResetLinkXml.contains("<i class="));

		String saveNotRefreshXml = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_DeliveryType__DeliveryType___saveNotRefresh").asXml();
		assertTrue(saveNotRefreshXml.contains("<i class=")); 
		assertFalse(saveNotRefreshXml.contains("images/save.gif"));		
	}
	
	private void actionInCorrectBar() { 
		String topBarText = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_DeliveryType__button_bar").asText();
		assertTrue(topBarText.contains("Save Refresh")); 
		assertTrue(topBarText.contains("Save not reset Save not refresh"));
		assertFalse(topBarText.contains("Assert combo deliveries"));
		
		String bottomBarText = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_DeliveryType__bottom_buttons").asText();
		assertEquals("Save Assert combo deliveries", bottomBarText);		
	}

	public void testPostmodifiyCalculatorNotOnRead() throws Exception {
		assertListNotEmpty();
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		String description = getValue("description");
		assertTrue("Description must have value", !Is.emptyString(description));
		execute("Mode.list");
		assertNoErrors();
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertValue("description", description); // No changed on read
	}
	
	public void testRemoveValidator_postcreateCalculator_postmodifyCalculator() throws Exception {
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "JUNIT");
		execute("CRUD.save");
		assertValue("number", ""); 
		assertValue("description", ""); 		
		assertNoErrors();
		assertMessage("Delivery type created successfully"); 
				
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("description", "JUNIT CREATED"); // 'CREATED' is added in postcreate
		setValue("description", "JUNIT");
		execute("CRUD.save");
		assertValue("number", ""); 
		assertValue("description", ""); 				
		assertNoErrors();
		assertMessage("Delivery type modified successfully");
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("description", "JUNIT MODIFIED"); // 'MODIFIED' is added in postmodify
		
		Delivery delivery = new Delivery();
		Invoice invoice = Invoice.findByYearNumber(2002, 1);
		delivery.setInvoice(invoice);
		DeliveryType deliveryType = XPersistence.getManager().find(DeliveryType.class, 66);		
		delivery.setType(deliveryType);
		delivery.setNumber(66);
		delivery.setDescription("JUNIT FOR DELIVERY TYPE");
		delivery.setDate(new java.util.Date());
		setDeliveryAdvice(delivery, "JUNIT ADVICE");
		XPersistence.getManager().persist(delivery);		
		XPersistence.commit();
				
		execute("CRUD.delete");		
		assertError("Delivery type 66 can not delete because it is used in deliveries");
		assertEditable("description"); // because return to main view (and controllers)
				
		delivery = XPersistence.getManager().merge(delivery); 
		XPersistence.getManager().remove(delivery);		
		XPersistence.commit();		
		
		execute("CRUD.delete");		
		assertNoErrors();
		assertMessage("Delivery type deleted successfully");		
	}
	
	static void setDeliveryAdvice(Delivery delivery, String advice) throws Exception  { 
		if (delivery.getClass().isAnnotationPresent(Entity.class)) {
			// In the JPA entity we use a @Transient and @Required for advice property
			// we need to put value to advice in order to pass
			// Hiberntate Validator constraint
			PropertiesManager pm = new PropertiesManager(delivery);
			pm.executeSet("advice", advice);
		}		
		// In OX2, advice is a view property then it does not exist in delivery 
	}

	private Delivery findDelivery() {
		return (Delivery) Delivery.findAll().iterator().next();
	}

					
}
