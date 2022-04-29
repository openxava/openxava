package org.openxava.test.calculators;


import org.openxava.calculators.ICalculator;
import org.openxava.hibernate.*;

public class ProductsValuesOfSubfamilyCalculator implements ICalculator {

	private int subfamilyNumber;

	public Object calculate() throws Exception {
		org.hibernate.Query query = XHibernate.getSession().createQuery("from Product2 where subfamily.number = :subfamilyNumber");
		query.setInteger("subfamilyNumber", getSubfamilyNumber());
		return query.list();
	}

	public int getSubfamilyNumber() {
		return subfamilyNumber;
	}

	public void setSubfamilyNumber(int subfamilyNumber) {
		this.subfamilyNumber = subfamilyNumber;
	}

}
