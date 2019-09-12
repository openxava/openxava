package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 *
 * @author Javier Paniza
 */

public class CustomerImageTest extends ModuleTestBase { 	
	
	public CustomerImageTest(String testName) {
		super(testName, "Customer");
	}
				
	public void testChangeImage() throws Exception {
		addImage();
		assertFile("photo"); 
	}
	
	public void testDeleteImage() throws Exception { 
		addImage();
		removeFile("photo");  				
		assertNoFile("photo");		
	}
	
	public void testCancelActionAfterChangeImageAction() throws Exception {   
		addImage();
		assertExists("telephone");
		assertAction("EditableOnOff.setOn"); 
		execute("Reference.createNew", "model=Seller,keyProperty=seller.number");		
		execute("NewCreation.cancel");
		assertExists("telephone");
		assertAction("EditableOnOff.setOn");		
	}
	
	public void testImageEditorFromAnotherModule() throws Exception {  	
		// started from a different module because there was a bug in imageEditor when run from a module
		//	that was not the initial
		changeModule("BeforeGoingToCustomer");
		execute("ChangeModule.goCustomer");
		
		// 
		testChangeImage(); 
	}
	
	protected void addImage() throws Exception{ 
		execute("CRUD.new");		
		uploadFile("photo", "test-images/foto_javi.jpg"); 
	}

}
