package org.openxava.calculators;

import org.openxava.util.*;

/**
 * Birth date day of the current user. <p>
 * 
 * @author Javier Paniza
 */

public class CurrentUserBirthDateDayCalculator implements ICalculator {

	public Object calculate() throws Exception {
		UserInfo user = Users.getCurrentUserInfo();
		return user.getBirthDateDay();
	}
	
}
