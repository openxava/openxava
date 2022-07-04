package org.openxava.calculators;

import org.openxava.util.*;

/**
 * Nick name of the current user. <p>
 * 
 * @author Javier Paniza
 */

public class CurrentUserNickNameCalculator implements ICalculator {

	public Object calculate() throws Exception {
		UserInfo user = Users.getCurrentUserInfo();
		return user.getNickName();
	}
	
}
