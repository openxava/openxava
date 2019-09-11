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
		/* tmp
		changeImage("photos.0.photo", "test-images/foto_javi.jpg");
		changeImage("photos.1.photo", "test-images/cake.gif");
		assertImage("photos.0.photo"); 
		assertImage("photos.1.photo");
		*/
		// tmp ini
		uploadFile("photos.0.photo", "test-images/foto_javi.jpg");
		uploadFile("photos.1.photo", "test-images/cake.gif");
		assertFile("photos.0.photo"); 
		assertFile("photos.1.photo");		
		// tmp fin
				
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("make", "ALFA ROMEO");
		assertValue("model", "MITO");
		assertValueInCollection("photos", 0, "description", "FRONT");
		assertValueInCollection("photos", 1, "description", "BACK");
		/* tmp
		assertImage("photos.0.photo");
		assertImage("photos.1.photo");
		
		removeImage("photos.0.photo");
		*/
		// tmp ini
		assertFile("photos.0.photo"); 
		assertFile("photos.1.photo");
		
		removeFile("photos.0.photo");		
		// tmp fin
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValueInCollection("photos", 0, "description", "FRONT");
		assertValueInCollection("photos", 1, "description", "BACK");
		/* tmp
		assertNoImage("photos.0.photo");
		assertImage("photos.1.photo");
		*/
		// tmp ini
		assertNoFile("photos.0.photo");
		assertFile("photos.1.photo");		
		// tmp fin
		
		setValueInCollection("photos", 1, "description", "");
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValueInCollection("photos", 0, "description", "FRONT");
		assertValueInCollection("photos", 1, "description", "");
		/* tmp
		assertNoImage("photos.0.photo");
		assertImage("photos.1.photo");
		*/
		// tmp ini
		assertNoFile("photos.0.photo");
		assertFile("photos.1.photo");		
		// tmp fin
		
		execute("CRUD.delete");
		assertNoErrors();
	}
		
}
