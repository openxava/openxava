package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ConferenceTest extends ModuleTestBase {
	
	public ConferenceTest(String testName) {
		super(testName, "Conference");		
	}
	
	public void testEmbeddedCollectionsOfSameType() throws Exception { 
		execute("CRUD.new");
		setValue("name", "THE OPENXAVA CONFERENCE"); 
		
		assertCollectionRowCount("mainTracks", 0);
		execute("Collection.new", "viewObject=xava_view_mainTracks");
		setValue("name", "INTRODUCTION TO OPENXAVA");
		execute("Collection.save");
		assertCollectionRowCount("mainTracks", 1);
		
		assertCollectionRowCount("secondaryTracks", 0);
		execute("Collection.new", "viewObject=xava_view_secondaryTracks");
		setValue("name", "CREATING CUSTOM EDITORS");
		execute("Collection.save");
		assertCollectionRowCount("secondaryTracks", 1);
		assertCollectionRowCount("mainTracks", 1);
		
		execute("Collection.edit", "row=0,viewObject=xava_view_mainTracks");
		assertNoErrors();
		assertValue("name", "INTRODUCTION TO OPENXAVA");
		closeDialog();
		
		assertCollectionRowCount("mainTracks", 1);
		assertValueInCollection("mainTracks", 0, 0, "INTRODUCTION TO OPENXAVA");
		execute("CollectionCopyPaste.cut", "row=0,viewObject=xava_view_mainTracks");
		assertNoAction("CollectionCopyPaste.paste", "viewObject=xava_view_mainTracks");
		assertCollectionRowCount("mainTracks", 1);
		
		assertCollectionRowCount("secondaryTracks", 1);
		assertValueInCollection("secondaryTracks", 0, 0, "CREATING CUSTOM EDITORS");
		execute("CollectionCopyPaste.paste", "viewObject=xava_view_secondaryTracks");
		assertCollectionRowCount("secondaryTracks", 2);
		assertValueInCollection("secondaryTracks", 0, 0, "CREATING CUSTOM EDITORS");
		assertValueInCollection("secondaryTracks", 1, 0, "INTRODUCTION TO OPENXAVA");
		assertCollectionRowCount("mainTracks", 0);	
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
}
