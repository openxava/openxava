package org.openxava.calculators;

import org.openxava.util.*;

/**
 * Family name of the current user. <p>
 * 
 * @author Javier Paniza
 */

public class CurrentUserFamilyNameCalculator implements ICalculator {

	public Object calculate() throws Exception {
		UserInfo user = Users.getCurrentUserInfo();
		return user.getFamilyName();
	}
	
}
