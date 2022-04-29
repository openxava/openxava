package org.openxava.test.model;

import java.util.*;
import javax.validation.constraints.*;
import org.openxava.annotations.*;

/**
 * Not persistent, only used for create a dialog for entry active year. <p>
 * 
 * Note that is not marked as @Entity <br>
 * 
 * @author Javier Paniza
 */
public class Year {
		
	@Max(9999) @Required
	private  int year;
	
	public Collection<Month> getMonths() {
		Collection<Month> result = new ArrayList<Month>();
		result.add(new Month("ENERO", 31));
		result.add(new Month("FEBRERO", 28));
		result.add(new Month("MARZO", 31));
		result.add(new Month("ABRIL", 30));
		result.add(new Month("MAYO", 31));
		result.add(new Month("JUNIO", 30));
		result.add(new Month("JULIO", 31));
		result.add(new Month("AGOSTO", 30));
		result.add(new Month("SEPTIEMBRE", 30));
		result.add(new Month("OCTUBRE", 31));
		result.add(new Month("NOVIEMBRE", 30));
		result.add(new Month("DICIEMBRE", 31));
		return result;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

}
