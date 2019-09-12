package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class CarTest extends ModuleTestBase {
	
	public CarTest(String testName) {
		super(testName, "Car");		
	}
	
	public void testImageInElementCollection_listFormattersForEnums() throws Exception {
		assertValueInList(0, "color", ": Red");
		
		execute("CRUD.new");		
		setValue("make", "ALFA ROMEO");
		setValue("model", "MITO");
		setValueInCollection("photos", 0, "description", "FRONT");
		setValueInCollection("photos", 1, "description", "BACK");
		uploadFile("photos.0.photo", "test-images/foto_javi.jpg");
		uploadFile("photos.1.photo", "test-images/cake.gif");
		assertFile("photos.0.photo"); 
		assertFile("photos.1.photo");		
				
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("make", "ALFA ROMEO");
		assertValue("model", "MITO");
		assertValueInCollection("photos", 0, "description", "FRONT");
		assertValueInCollection("photos", 1, "description", "BACK");
		assertFile("photos.0.photo"); 
		assertFile("photos.1.photo");
		
		removeFile("photos.0.photo");		
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValueInCollection("photos", 0, "description", "FRONT");
		assertValueInCollection("photos", 1, "description", "BACK");
		assertNoFile("photos.0.photo");
		assertFile("photos.1.photo");		
		
		setValueInCollection("photos", 1, "description", "");
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValueInCollection("photos", 0, "description", "FRONT");
		assertValueInCollection("photos", 1, "description", "");
		assertNoFile("photos.0.photo");
		assertFile("photos.1.photo");		
		
		execute("CRUD.delete");
		assertNoErrors();
	}
		
}
