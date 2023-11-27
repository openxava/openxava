package org.openxava.test.bymodule;

import org.openxava.jpa.*;
import org.openxava.tests.ModuleTestBase;

/**
 * 
 * @author Javier Paniza
 */
public class AsEmbedContainerTest extends ModuleTestBase {
	
	public AsEmbedContainerTest(String testName) {
		super(testName, "AsEmbedContainer");		
	}

	public void test3LevelAsEmbeddedEntities_hideMemberInEmbeddedInsideDialog() throws Exception {
		deleteData();
		execute("CRUD.new");
		setValue("value", "CONTAINER");
		execute("Collection.new", "viewObject=xava_view_children");
		setValue("value1", "VALUE 1");
		setValue("asEmbed2.value2", "VALUE 2");
		setValue("asEmbed2.asEmbed3.value3", "VALUE 3");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("children", 1);
		execute("Collection.edit", "row=0,viewObject=xava_view_children");
		assertValue("value1", "VALUE 1");
		assertValue("asEmbed2.value2", "VALUE 2");
		assertValue("asEmbed2.asEmbed3.value3", "VALUE 3");
		
		setValue("asEmbed2.value2", "HIDE");
		assertNotExists("asEmbed2.value2");

		changeModule("AsEmbed2");
		assertListRowCount(1);
		
		changeModule("AsEmbed3");
		assertListRowCount(1);
	}
	
	private void deleteData() {
		XPersistence.getManager().createQuery("delete from AsEmbed1").executeUpdate();
		XPersistence.getManager().	createQuery("delete from AsEmbed2").executeUpdate();
		XPersistence.getManager().createQuery("delete from AsEmbed3").executeUpdate();
		XPersistence.getManager().createQuery("delete from AsEmbedContainer").executeUpdate();		
		XPersistence.commit();
	}

}
