package org.openxava.calculators;

import org.openxava.util.*;

/**
 * Middle name of the current user. <p>
 * 
 * Outside of a portal it will be an empty string. <br>
 * 
 * @author Javier Paniza
 */

public class CurrentUserMiddleNameCalculator implements ICalculator {

	public Object calculate() throws Exception {
		UserInfo user = Users.getCurrentUserInfo();
		return user.getMiddleName();
	}
	
}
