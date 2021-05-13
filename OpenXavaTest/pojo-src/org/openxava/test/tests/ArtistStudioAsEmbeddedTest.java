package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * tmp
 * @author Javier Paniza
 */

public class ArtistStudioAsEmbeddedTest extends ModuleTestBase {
	
	public ArtistStudioAsEmbeddedTest(String testName) {
		super(testName, "ArtistStudioAsEmbedded");		
	}
	
	public void testCreateCollectionElementInAnEmbeddedReference() throws Exception {
		// It failed with Artist and its Studio, but not in other cases like Invoice with Customer and DeliverPlace,
		// perhaps because Artist and Studio are Identifiable
		execute("CRUD.new");
		setValue("name", "JUNIT MAIN ARTIST");
		setValue("artistStudio.name", "JUNIT STUDIO");
		execute("Collection.new", "viewObject=xava_view_artistStudio_artists");
		setValue("name", "JUNIT SECONDARY ARTIST");
		execute("Collection.save");
		assertNoErrors();
		// tmp Comprobar también que todo se ha grabado y borrar los datos
	}
			
}
