package org.openxava.web;

import org.openxava.util.*;

/**
 * Utility class to help in action code generated for JSPs.
 * 
 * Create on 30/10/2009 (16:32:16)
 * @author Ana Andrés
 */
public class Actions {
	
	public static String getActionOnChangeComparator(String id,String idConditionValue,String idConditionValueTo) {
		return "onChange=\"openxava.onChangeComparator(" +
			"'" + id + "'," +
			"'" + idConditionValue + "'," +
			"'" + idConditionValueTo + "'," +
			"'" + XavaResources.getString("from") + "'," +
			"'" + XavaResources.getString("in_values") + "'" + 
			")\"";
	}
	
}
