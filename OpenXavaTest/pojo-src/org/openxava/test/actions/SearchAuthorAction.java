package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class SearchAuthorAction extends SearchByViewKeyAction implements IJavaScriptPostAction { 
	
	private String author; 
	
	public void execute() throws Exception {
		super.execute();
		author = getView().getValueString("author");
		addMessage("showing_author", author);
	}

	public String getPostJavaScript() { 
		return "$('#module_title').html('Author - " + author + "')";
	}

}
