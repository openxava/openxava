package com.yourcompany.yourapp.tests;

import org.openxava.tests.*;

public class PlanTest extends ModuleTestBase {

	public PlanTest(String nameTest) {
		super(nameTest, "yourapp", "Plan");
	}
	
	public void testCreateNewPlan() throws Exception {
		login("admin", "admin");
		assertListRowCount(2);
		execute("CRUD.new");
		
		String [][] workers = {
			{ "", "" },
			{ "2c94f081900875e80190088fd8f60004", "Javi" },
			{ "2c94f081900875e8019008901a180005", "Peter" }
		};
		assertValidValues("worker.id", workers);
		setValue("worker.id", "2c94f081900875e8019008901a180005"); // Peter 
		
		String [][] periods = {
			{ "", "" },
			{ "2c94f081900875e80190089394730006", "2024.10" },
			{ "2c94f081900875e801900893a5e90007", "2024.11" }
		};
		assertValidValues("period.id", periods);
		setValue("period.id", "2c94f081900875e801900893a5e90007"); // 2024.11 
		
		execute("Collection.new", "viewObject=xava_view_issues");
		setValue("title", "The first step of my big plan");
		setValue("type.id", "2c94f081900875e801900896f25b0008"); // Bug 
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("issues", 1);
		
		execute("Mode.list");
		assertListRowCount(3);
		assertValueInList(1, 0, "Peter");
		assertValueInList(1, 1, "2024.11");
		
		execute("List.viewDetail", "row=1"); 
		assertValue("worker.id", "2c94f081900875e8019008901a180005"); // Peter
		assertValue("period.id", "2c94f081900875e801900893a5e90007"); // 2024.11		
		assertCollectionRowCount("issues", 1);
		assertValueInCollection("issues", 0, 0, "The first step of my big plan");
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_issues");
		execute("CRUD.delete");
		assertNoErrors();
		
		changeModule("Issue");
		assertListRowCount(1);
		assertValueInList(0, 0, "The first step of my big plan");
		assertValueInList(0, 1, "Bug");
		checkAll();
		execute("CRUD.deleteSelected");
		assertNoErrors();
	}

}
