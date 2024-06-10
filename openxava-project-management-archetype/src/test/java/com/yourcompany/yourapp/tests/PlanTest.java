package com.yourcompany.yourapp.tests;

import org.openxava.tests.*;

public class PlanTest extends ModuleTestBase {

	public PlanTest(String nameTest) {
		super(nameTest, "xavaprojects", "Plan");
	}
	
	public void testCreateNewPlan() throws Exception {
		login("admin", "admin");
		assertListRowCount(2);
		execute("CRUD.new");
		
		String [][] workers = {
			{"", ""},
			{"4028808d7eea19fe017eea56ed120018", "Javi"},
			{"4028808d7eea19fe017eea56ffeb0019", "Pedro"}
		};
		assertValidValues("worker.id", workers);
		setValue("worker.id", "4028808d7eea19fe017eea56ffeb0019"); // Pedro 
		
		String [][] periods = {
			{"", ""},
			{"4028808d7eea19fe017eea5a84e5001a", "2019.10"},
			{"4028808d7eea19fe017eea5ab6dc001b", "2019.11"}
		};
		assertValidValues("period.id", periods);
		setValue("period.id", "4028808d7eea19fe017eea5ab6dc001b"); // 2019.11 
		
		execute("Collection.new", "viewObject=xava_view_issues");
		setValue("title", "The first step of my big plan");
		setValue("type.id", "4028808d7eea19fe017eea61bec90024"); // Bug 
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("issues", 1);
		
		execute("Mode.list");
		assertListRowCount(3);
		assertValueInList(1, 0, "Pedro");
		assertValueInList(1, 1, "2019.11");
		
		execute("List.viewDetail", "row=1"); 
		assertValue("worker.id", "4028808d7eea19fe017eea56ffeb0019"); // Pedro
		assertValue("period.id", "4028808d7eea19fe017eea5ab6dc001b"); // 2019.11		
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
