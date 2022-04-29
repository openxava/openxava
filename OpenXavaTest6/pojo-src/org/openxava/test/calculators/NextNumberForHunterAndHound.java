package org.openxava.test.calculators;

import org.openxava.calculators.ICalculator;
import org.openxava.jpa.XPersistence;

import javax.persistence.Query;

@SuppressWarnings("serial")
public class NextNumberForHunterAndHound implements ICalculator {
	
	@Override
	public Object calculate() throws Exception {
		Query query = XPersistence.getManager().createQuery(
			"select max(h.number) from HunterAndHound h");
		Integer nextNumber = (Integer) query.getSingleResult();
		return nextNumber == null ? 1 : nextNumber + 1;
	}
}
