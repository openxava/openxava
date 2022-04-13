package org.openxava.tab.impl;



import org.openxava.calculators.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;


/**
 *@author Javier Paniza
 */

public class TabCalculator implements java.io.Serializable {
	private int index;
	private String propertyName;
	private MetaCalculator metaCalculator;
	private ICalculator calculator;
	private PropertiesManager propertiesManager;
	

	public TabCalculator(MetaProperty metaProperty, int propertyIndex)
		throws XavaException {
		this.index = propertyIndex;
		this.propertyName = metaProperty.getQualifiedName();
		this.metaCalculator = metaProperty.getMetaCalculator();
		this.calculator = metaCalculator.createCalculator();
		this.propertiesManager = new PropertiesManager(calculator);
	}

	public ICalculator getCalculator() {
		return calculator;
	}

	public int getIndex() {
		return index;
	}

	public PropertiesManager getPropertiesManager() {
		return propertiesManager;
	}

	public MetaCalculator getMetaCalculator() {
		return metaCalculator;
	}

	public String getPropertyName() {
		return propertyName;
	}

}

