package org.openxava.test.tests;

/**
 *
 * @author Javier Paniza
 */

public class Product5Test extends CustomizeListTestBase { 
	
	public Product5Test(String testName) {
		super(testName, "Product5");		
	}
	
	public void testValidationFromSetterOnCreate() throws Exception { 
		execute("CRUD.new");
		setValue("number", "666");
		setValue("description", "OPENXAVA");
		setValue("family", "2");
		setValue("subfamily.number", "12");
		setValue("unitPrice", "300");
		execute("CRUD.save");
		assertError("You cannot sell OpenXava");
		assertErrorsCount(1);
		setValue("description", "ECLIPSE");
		execute("CRUD.save");
		assertError("You cannot sell Eclipse");
		assertErrorsCount(1);
	}

	
	// This case can be only reproduced in custom dialog with Product5 (in other Product it works)
	public void testDialogActionsAreNotLost() throws Exception {  
		execute("ExtendedPrint.myReports"); 
		assertValueInCollection("columns", 10, 0, "Unit price"); 
		assertValueInCollection("columns", 10, 4, ""); 
		execute("MyReport.editColumn", "row=10,viewObject=xava_view_columns"); 
		setValue("sum", "true");
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 10, 4, "Sum"); 
		assertAction("MyReport.generatePdf");
	}
	
	public void testDescriptionsListDependsOnEnum() throws Exception {
		
		execute("CRUD.new");
	
		// Verifying initial state
		String [][] familyValues = {
			{ "", "" },
			{ "0", "NONE" },
			{ "1", "SOFTWARE" },
			{ "2", "HARDWARE" },
			{ "3", "SERVICIOS" }
		};
		
		assertValidValues("family", familyValues);
		setValue("family", "0");
		
		String [][] voidValues = {
			{ "", "" }
		};
		
		assertValue("subfamily.number", "");
		assertValidValues("subfamily.number", voidValues);
		
		// Change value
		setValue("family", "2");
		String [][] hardwareValues = {
			{ "", "" },
			{ "12", "PC" },
			{ "13", "PERIFERICOS" },
			{ "11", "SERVIDORES" }
		};
		assertValue("subfamily.number", "");
		assertValidValues("subfamily.number", hardwareValues);
		
		// Changing the value again
		setValue("family", "1");
		String [][] softwareValues = {
			{ "", "" },
			{ "1", "DESARROLLO" },
			{ "2", "GESTION" },
			{ "3", "SISTEMA" }
		};
		assertValue("subfamily.number", "");
		assertValidValues("subfamily.number", softwareValues);
	}
	
	
	public void testCollectionWithLongNameStoresPreferences() throws Exception { 
		execute("CRUD.new");
		assertCollectionColumnCount("productDetailsSupplierContactDetails", 2);
		removeColumn("productDetailsSupplierContactDetails", 1);  
		assertCollectionColumnCount("productDetailsSupplierContactDetails", 1);
		
		resetModule();
		
		execute("CRUD.new");
		assertCollectionColumnCount("productDetailsSupplierContactDetails", 1); 
		execute("List.addColumns", "collection=productDetailsSupplierContactDetails");
		execute("AddColumns.restoreDefault");
		assertCollectionColumnCount("productDetailsSupplierContactDetails", 2); 
		
		resetModule();
		
		execute("CRUD.new");
		assertCollectionColumnCount("productDetailsSupplierContactDetails", 2);
	}

	public void testRememberActionsInList_browserReloadOnMultipartForm() throws Exception {  
		String[] listActions = {
			"CRUD.new", "CRUD.deleteRow", "CRUD.deleteSelected",
			"Print.generatePdf", "Print.generateExcel",
			"ImportData.importData", 
			"List.filter", "List.sumColumn", "List.orderBy", "List.hideRows", "List.changeConfiguration", 
			"List.viewDetail", "List.changeColumnName",  
			"ExtendedPrint.myReports",
			"ListFormat.select",
			"Product5.goB"
		};
		String[] detailActions = {
			"Navigation.previous", "Navigation.first", "Navigation.next",
			"CRUD.delete", "CRUD.new", "CRUD.refresh", "CRUD.save", 
			"Mode.list", 
			"List.filter", "List.changeColumnName", "Print.generatePdf",  
			"Collection.removeSelected", "CollectionCopyPaste.cut", "List.orderBy", "Collection.new",  
			"Reference.createNew", "Reference.modify", "Print.generateExcel", 
			"Product5.seeInitial",
			"Product5.addPhoto" 
		};
		
		String[] loadPhotoActions = { 
			"Mode.list", "LoadPhotoIntoGallery.loadPhoto", "LoadPhotoIntoGallery.cancel" 	
		};
		
		// list -> detail -> list
		assertActions(listActions); 
		execute("Product5.goB");
		assertAction("Product5.goA");
		assertNoAction("Product5.goB");
		
		execute("List.viewDetail", "row=0");
		assertActions(detailActions); 
		
		execute("Mode.list");
		assertAction("Product5.goA"); 
		assertNoAction("Product5.goB");
		assertAction("CRUD.new");
		
		// list -> detail -> custom view -> list
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		execute("Product5.addPhoto"); 
		assertActions(loadPhotoActions); 
		
		
		execute("Mode.list");
		assertAction("Product5.goA"); 
		assertNoAction("Product5.goB");
		assertAction("CRUD.new");
		
		execute("List.viewDetail", "row=0");
		assertFilesCount("photos", 0);
		execute("Product5.addPhoto"); 
		assertActions(loadPhotoActions);		
		reload();
		assertActions(loadPhotoActions);
		String imageUrl = System.getProperty("user.dir") + "/test-images/foto_javi.jpg";
		setFileValue("newFile", imageUrl);
		execute("LoadPhotoIntoGallery.loadPhoto");
		assertMessage("Image added to the gallery"); 
		assertFilesCount("photos", 1);
		removeFile("photos", 0);		
	}
	
	
	/* it fails after you execute an action with addActions or removeActions */
	public void testDialogAfterAddRemoveActions() throws Exception {
		assertAction("Product5.goB");
		execute("List.viewDetail", "row=0");
		assertAction("Navigation.first");
		execute("Product5.seeInitial");
		assertNoErrors();
		assertAction("Dialog.cancel");
		execute("Dialog.cancel");
		assertAction("Navigation.first");
		
		assertValue("unitPrice", "11.0000"); // We test that has 4 decimals, the specific value does not matter
	}
}
