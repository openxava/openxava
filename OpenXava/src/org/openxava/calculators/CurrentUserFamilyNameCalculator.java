package org.openxava.calculators;

import org.openxava.util.*;

/**
 * Family name of the current user. <p>
 * 
 * Outside of a portal it will be an empty string. <br>
 * 
 * @author Javier Paniza
 */

public class CurrentUserFamilyNameCalculator implements ICalculator {

	public Object calculate() throws Exception {
		UserInfo user = Users.getCurrentUserInfo();
		return user.getFamilyName();
	}
	
}
