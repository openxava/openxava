package org.openxava.test.tests.bymodule;

/**
 *  
 * @author Javier Paniza
 */
public class ChampionshipScoreTest extends CustomizeListTestBase {
	
	public ChampionshipScoreTest(String testName) {
		super(testName, "ChampionshipScore");		
	}
	
	public void testAddCollectionElementWithRequiredReferenceNotInCollectionView() throws Exception {
		// CRUD.new not needed because there is no element
		setValue("championship", "JUNIT CHAMPIONSHIP");
		// drive (required) not set, needed to test the case
		execute("Collection.add", "viewObject=xava_view_racesScores");
		assertListRowCount(1);
		execute("AddToCollection.add", "row=0");
		assertError("Value for Driver in Championship score is required"); // To test that UI is no broken
	}
		
}
