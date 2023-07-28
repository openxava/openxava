package org.openxava.web.dwr;

import org.openxava.application.meta.*;

/**
 * To parse the id of a HTML table.
 * 
 * @since 5.3
 * @author Javier Paniza 
 */
class TableId { 
	
	private String application;
	private String module;
	private String tabObject;
	private String collection;
	private boolean valid;
	
	public TableId(String tableId, int additionalTokens) {
		application = MetaApplications.getMainMetaApplication().getName();
		String shortTableId = tableId.replace(application, "");
		String [] id = shortTableId.split("_+");
		if (!"ox".equals(id[0])) {
			// Bad format. This method relies in the id format by Ids class
			valid = false;
			return;
		}		

		module = id[1];
		StringBuffer collectionSB = new StringBuffer();
		for (int i=2; i<id.length-additionalTokens; i++) { // To work with collections inside @AsEmbedded references
			if (i>2) collectionSB.append("_");
			collectionSB.append(id[i]);
		}
		this.collection = collectionSB.toString();
		tabObject = "list".equals(collection)?"xava_tab":"xava_collectionTab_" + collection;
		valid = true;		
	}

	public String getApplication() {
		return application;
	}

	public String getModule() {
		return module;
	}

	public String getTabObject() {
		return tabObject;
	}
	
	public String getCollection() {
		return collection;
	}
	
	public boolean isValid() {
		return valid;
	}
	
}
