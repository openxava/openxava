package org.openxava.actions;

/**
 * 
 * @author Genaro Tur 
 */


public class CreateNewElementInManyToManyCollectionAction extends CollectionBaseAction  {
	
	public void execute() throws Exception {
        String collectionModel = getCollectionElementView().getModelName();
        String collection = getCollectionElementView().getMemberName();
    	showDialog();    	
        getView().setModelName(collectionModel);
        getView().setMemberName(collection);
        setControllers("ManyToManyNewElement", "Dialog");  
    }
	
}
