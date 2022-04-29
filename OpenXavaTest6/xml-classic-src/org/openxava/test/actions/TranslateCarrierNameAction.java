package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.test.model.*;
import org.openxava.util.*;



/**
 * @author Javier Paniza
 */
public class TranslateCarrierNameAction extends CollectionBaseAction {
	
	private boolean all = false;
	private String targetLanguage;
	

	public void execute() throws Exception {
		Iterator it = (all?getObjects():getSelectedObjects()).iterator();
		while (it.hasNext()) {
			ICarrier carrier = (ICarrier) it.next();			
			// An 'carrier.translate(targetLanguage)' is a good alterantive 
			if ("ES".equalsIgnoreCase(targetLanguage)) carrier.translateToSpanish();
			else if ("EN".equalsIgnoreCase(targetLanguage)) carrier.translateToEnglish();
			else carrier.translate();
		}		
		if ("EN".equalsIgnoreCase(targetLanguage)) {
			getCollectionElementView().addListAction("Carrier.todosAEspanol");
			getCollectionElementView().removeListAction("Carrier.allToEnglish");
		}
		else if ("ES".equalsIgnoreCase(targetLanguage)) {
			getCollectionElementView().addListAction("Carrier.allToEnglish");
			getCollectionElementView().removeListAction("Carrier.todosAEspanol");
		}
	}

	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}
	
	public String getTargetLanguage() {
		return targetLanguage;
	}

	public void setTargetLanguage(String targetLanguage) {
		if (!(Is.empty(targetLanguage) || 
			"ES".equalsIgnoreCase(targetLanguage) ||
			"EN".equalsIgnoreCase(targetLanguage))) {
			throw new IllegalArgumentException("Only 'ES' or 'EN' are allowed for 'targetLanguage'");
		}
		this.targetLanguage = targetLanguage;
	}	
	
}
