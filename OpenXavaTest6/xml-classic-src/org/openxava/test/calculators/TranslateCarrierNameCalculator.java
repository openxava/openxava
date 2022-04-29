package org.openxava.test.calculators;

import java.rmi.*;

import org.openxava.calculators.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class TranslateCarrierNameCalculator implements IModelCalculator {
	
	private ICarrier carrier;
	private String targetLanguage;

	public Object calculate() throws Exception {				
		String name = carrier.getName();
		String translated = name;
		if (Is.emptyString(targetLanguage) || "EN".equalsIgnoreCase(targetLanguage)) {
			if ("UNO".equals(name)) translated = "ONE";
			else if ("DOS".equals(name)) translated ="TWO";
			else if ("TRES".equals(name)) translated ="THREE";
			else if ("CUATRO".equals(name)) translated ="FOUR";
			else if ("CINCO".equals(name)) translated ="FIVE";
		}
		
		if (Is.emptyString(targetLanguage) || "ES".equalsIgnoreCase(targetLanguage)) {
			if ("ONE".equals(name)) translated = "UNO";
			else if ("TWO".equals(name)) translated ="DOS";
			else if ("THREE".equals(name)) translated ="TRES";
			else if ("FOUR".equals(name)) translated ="CUATRO";
			else if ("FIVE".equals(name)) translated ="CINCO";
		}
		carrier.setName(translated);
		return null;
	}

	public void setModel(Object entity) throws RemoteException {
		this.carrier = (ICarrier) entity;		
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
