package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class SearchAuthorAction extends SearchByViewKeyAction implements IJavaScriptPostAction { /* tmp implements IJavaScriptPostAction */
	
	private String author; // tmp
	
	public void execute() throws Exception {
		super.execute();
		// tmp addMessage("showing_author", getView().getValue("author"));
		// tmp ini
		System.out.println("[SearchAuthorAction.execute] "); // tmp
		author = getView().getValueString("author");
		addMessage("showing_author", author);
		// tmp fin
	}

	public String getPostJavaScript() { // tmp
		return "$('#module_title').html('Author - " + author + "')";
	}

}
