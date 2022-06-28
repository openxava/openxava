package org.openxava.calculators;

import org.openxava.util.*;

/**
 * Email of the current user. <p>
 * 
 * @author Javier Paniza
 */

public class CurrentUserEmailCalculator implements ICalculator {

	public Object calculate() throws Exception {
		UserInfo user = Users.getCurrentUserInfo();
		return user.getEmail();
	}
	
}
