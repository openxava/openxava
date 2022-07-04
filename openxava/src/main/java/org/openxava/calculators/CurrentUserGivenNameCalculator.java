package org.openxava.calculators;

import org.openxava.util.*;

/**
 * Given name of the current user. <p>
 * 
 * @author Javier Paniza
 */

public class CurrentUserGivenNameCalculator implements ICalculator {

	public Object calculate() throws Exception {
		UserInfo user = Users.getCurrentUserInfo();
		return user.getGivenName();
	}
	
}
