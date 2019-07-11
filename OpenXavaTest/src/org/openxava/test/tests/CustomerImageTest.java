package org.openxava.test.tests;

/**
 * 
 * @author Javier Paniza
 */

public class CustomerImageTest extends ImageTestBase { 	
	
	public CustomerImageTest(String testName) {
		super(testName, "Customer");
	}
				
	public void testChangeImage() throws Exception {
		addImage();
		assertImage("photo"); 
	}
	
	public void testDeleteImage() throws Exception { 
		addImage();
		/* tmp 
		execute("ImageEditor.deleteImage", "newImageProperty=photo");
		assertNoErrors();
		*/
		// tmp ini
		getHtmlPage().executeJavaScript(
			"var xhr = new XMLHttpRequest();" +
			"xhr.open('DELETE', imageEditor.getUploadURL(input));" +
			"xhr.send(null);"				
		);
		waitAJAX();
		// tmp fin
				
		assertNoImage("photo"); 
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
		changeImage("photo", "/test-images/foto_javi.jpg");	
	}

}
