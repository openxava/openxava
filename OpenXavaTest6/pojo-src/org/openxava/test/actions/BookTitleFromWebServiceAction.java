package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;
import org.openxava.test.services.*;

/**
 * 
 * @author Javier Paniza
 */

public class BookTitleFromWebServiceAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		Book book = BookService.get();
		getView().setValue("title", book.getTitle());
	}
	
}
