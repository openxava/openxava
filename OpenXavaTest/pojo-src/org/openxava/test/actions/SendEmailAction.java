package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class SendEmailAction extends BaseAction {

	public void execute() throws Exception {
		Emails.send("fulano@example.com", "This is an email message", "Hi, I'm OpenXava");
	}

}
