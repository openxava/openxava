package org.openxava.session;

import java.io.*;
import java.util.prefs.*;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.annotations.*;
import org.openxava.util.*;

/**
 * @author Federico Alcantara
 *
 */
@Embeddable
public class ChartColumn implements Serializable {

	private static final long serialVersionUID = 1L;

	private final static String COLUMN = "column";
	private final static String NAME = "name";	
	private final static String NUMBER = "number";
			
	@Hidden
	private Chart chart;
		
	@OnChange(OnChangeChartColumnNameAction.class)
	@Required
	private String name;
		
	@Hidden
	private boolean number;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart report) {
		this.chart = report;
	}

	public void save(Preferences preferences, int index) { 		
		preferences.put(COLUMN + index + "." + NAME, name);			
		preferences.put(COLUMN + index + "." + NUMBER, Boolean.toString(number));
	}

	public boolean load(Preferences preferences, int index) throws BackingStoreException {   
		String name = preferences.get(COLUMN + index + "." + NAME, null);
		if (name == null) return false;
		this.name = name;		
		String number = preferences.get(COLUMN + index + "." + NUMBER, null);
		this.number = number == null?false:Boolean.valueOf(number);
		return true;
	}
	
	public static boolean remove(Preferences preferences, int index) { 
		String name = preferences.get(COLUMN + index + "." + NAME, null);
		if (name == null) return false;
		preferences.remove(COLUMN + index + "." + NAME);
		preferences.remove(COLUMN + index + "." + NUMBER);
		return true;
	}

	public String getLabel() { 
		return Labels.get(name); 
	}

	public boolean isNumber() {
		return number;
	}

	public void setNumber(boolean number) {
		this.number = number;
	}
	
	
	
}
