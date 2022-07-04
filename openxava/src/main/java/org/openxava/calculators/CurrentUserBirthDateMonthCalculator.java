package org.openxava.calculators;

import org.openxava.util.*;

/**
 * Birth date month of the current user. <p>
 * 
 * @author Javier Paniza
 */

public class CurrentUserBirthDateMonthCalculator implements ICalculator {

	public Object calculate() throws Exception {
		UserInfo user = Users.getCurrentUserInfo();
		return user.getBirthDateMonth();
	}
	
}
