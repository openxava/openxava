package org.openxava.test.calculators;

import java.util.*;

import org.openxava.calculators.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class MonthsCalculator implements ICalculator {

	public Object calculate() throws Exception {
		Collection<Month> result = new ArrayList<Month>();		
		add(result, "ENERO", 31);
		add(result, "FEBRERO", 28);
		add(result, "MARZO", 31);
		add(result, "ABRIL", 30);
		add(result, "MAYO", 31);
		add(result, "JUNIO", 30);
		add(result, "JULIO", 31);
		add(result, "AGOSTO", 30);
		add(result, "SEPTIEMBRE", 30);
		add(result, "OCTUBRE", 31);
		add(result, "NOVIEMBRE", 30);
		add(result, "DICIEMBRE", 31);
		return result;
	}
	
	private void add(Collection<Month> months, String name, int days) {
		Month month = new Month();
		month.setName(name);
		month.setDays(days); 
		months.add(month);		
	}

}
