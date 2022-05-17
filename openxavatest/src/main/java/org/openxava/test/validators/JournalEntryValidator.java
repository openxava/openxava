package org.openxava.test.validators;

import org.openxava.test.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * 
 * @author Javier Paniza 
 */
public class JournalEntryValidator implements IValidator {

	private Journal theJournal;
	
	public void validate(Messages errors)  {			
		if (theJournal == null) {
			errors.add("journal_required"); 
		}				
	}

	public Journal getTheJournal() {
		return theJournal;
	}

	public void setTheJournal(Journal theJournal) {
		this.theJournal = theJournal;
	}

}
