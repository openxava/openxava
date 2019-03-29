package org.openxava.test.tests;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class CommunityTest extends ModuleTestBase {
	
	public CommunityTest(String testName) {
		super(testName, "Community");		
	}
	
	public void testPolimorphingEditionOfCollectionElements() throws Exception { 		
		execute("List.viewDetail", "row=0");
		assertValue("name", "PROGRAMMERS");
		assertCollectionRowCount("members", 3);
		execute("Collection.edit", "row=1,viewObject=xava_view_members"); 
		assertValue("name", "JAVI");
		assertValue("favouriteFramework", "OPENXAVA");
		closeDialog();

		execute("Collection.edit", "row=2,viewObject=xava_view_members"); 
		assertValue("name", "JUANJO");
		assertNotExists("favouriteFramework");
		assertValue("mainLanguage", "RPG");
	}
	
	public void testManyToManyNewElement() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("name", "PROGRAMMERS");
		assertCollectionRowCount("members", 3);
		assertNoAction("Collection.new"); 
		execute("ManyToMany.new", "viewObject=xava_view_members");
		setValue("name", "JUNIT");
		setValue("sex", String.valueOf(Human.Sex.FEMALE.ordinal()));
		execute("ManyToManyNewElement.save");
		assertCollectionRowCount("members", 4);
		assertValueInCollection("members", 3, "name", "JUNIT");
		assertValueInCollection("members", 3, "sex", "Female");
		
		checkRowCollection("members", 3);
		execute("Collection.removeSelected", "viewObject=xava_view_members");
		assertCollectionRowCount("members", 3);
		assertNoErrors();
		
		XPersistence
			.getManager()
			.createQuery("delete from Human h where h.name = 'JUNIT'")
			.executeUpdate(); 
	}
	
}
