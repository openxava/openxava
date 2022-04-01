package org.openxava.calculators;

import org.openxava.calculators.ICalculator;

import java.util.Date;

/**
 * @author Janesh Kodikara
 */

public class DaysDelayCalculator implements ICalculator {

	private Date startDate;
	private Date endDate;
	int delay;

	public Object calculate() throws Exception {
		if (startDate == null || endDate == null)
			return new Integer(0);
		delay = (int) (endDate.getTime() - startDate.getTime())
				/ (1000 * 60 * 60 * 24);
		return new Integer(delay);
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
