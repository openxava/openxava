package org.openxava.generators;

import java.io.*;
import java.util.*;



import org.openxava.util.*;

/**
 * Utility methods used in code generation.
 * 
 * @author Javier Paniza
 */
public class Generators {

	private static Collection availableLocales = null;
	
	public static String generateCast(String type, String sentence) throws XavaException {
		if (type == null) {
			throw new XavaException("cast_type_required", sentence); 
		}
		type = type.trim();
		if (type.equals("boolean")) {
			return "((Boolean) " + sentence + ").booleanValue()";
		}
		else if (type.equals("byte")) {
			return "((Byte) " + sentence + ").byteValue()";
		}
		else if (type.equals("char")) {
			return "((Character) " + sentence + ").charValue()";
		}
		else if (type.equals("short")) {
			return "((Short) " + sentence + ").shortValue()";
		}
		else if (type.equals("int")) {
			return "((Integer) " + sentence + ").intValue()";
		}
		else if (type.equals("long")) {
			return "((Long) " + sentence + ").longValue()";
		}
		else if (type.equals("float")) {
			return "((Float) " + sentence + ").floatValue()";
		}
		else if (type.equals("double")) {
			return "((Double) " + sentence + ").doubleValue()";
		}
		else {
			return "(" + type + ") " + sentence;
		}
	}
	
	public static String generatePrimitiveWrapper(String type, String sentence) throws XavaException {
		if (type == null) {
			throw new XavaException("cast_type_required", sentence); 
		}
		type = type.trim();
		if (type.equals("boolean")) {
			return "new Boolean(" + sentence + ")";
		}
		else if (type.equals("byte")) {
			return "new Byte(" + sentence + ")";
		}
		else if (type.equals("char")) {
			return "new Character(" + sentence + ")";
		}
		else if (type.equals("short")) {
			return "new Short(" + sentence + ")";
		}
		else if (type.equals("int")) {
			return "new Integer(" + sentence + ")";
		}
		else if (type.equals("long")) {
			return "new Long(" + sentence + ")";
		}
		else if (type.equals("float")) {
			return "new Float(" + sentence + ")";
		}
		else if (type.equals("double")) {
			return "new Double(" + sentence + ")";
		}
		else {
			return sentence;
		}
	}
	
	public static String generateCastFromString(String type, String sentence) throws XavaException {
		if (type == null) {
			throw new XavaException("to_string_type_required", sentence); 
		}
		type = type.trim();
		if (type.equals("boolean")) {			
			return "Boolean.valueOf(" + sentence + ").booleanValue()";
		}
		else if (type.equals("byte")) {			
			return "Byte.parseByte(" + sentence + ")";
		}
		else if (type.equals("char")) {
			return sentence + ".length() > 0?" + sentence + ".charAt(0):' '";
		}
		else if (type.equals("short")) {						
			return "Short.parseShort(" + sentence + ")";
		}
		else if (type.equals("int")) {
			return "Integer.parseInt(" + sentence + ")";			
		}
		else if (type.equals("long")) {
			return "Long.parseLong(" + sentence + ")";			
		}
		else if (type.equals("float")) {			
			return "Float.parseFloat(" + sentence + ")";
		}
		else if (type.equals("double")) {
			return "Double.parseDouble(" + sentence + ")";			
		}
		else if (type.equals("java.math.BigDecimal") || type.equals("BigDecimal")) {
			return "new BigDecimal(" + sentence + ")";
		}
		else {
			return sentence;
		}
	}
	
	public static String getWebsphereSQLType(String javaTypeName, boolean href) throws XavaException {
		if ("String".equals(javaTypeName) || "java.lang.String".equals(javaTypeName)) {
			return href?"SQL92_Primitives.xmi#SQLCharacterStringType_2":"RDBSchema:SQLCharacterStringType";
		}
		if ("int".equals(javaTypeName) || "Integer".equals(javaTypeName) || "java.lang.Integer".equals(javaTypeName)) {
			return href?"SQL92_Primitives.xmi#SQLExactNumeric_1":"RDBSchema:SQLExactNumeric";
		}
		if ("java.math.BigDecimal".equals(javaTypeName)) {
			return href?"SQL92_Primitives.xmi#SQLNumeric_1":"RDBSchema:SQLNumeric";
		}
		if ("java.util.Date".equals(javaTypeName) || "java.sql.Date".equals(javaTypeName)) {
			return href?"SQL92_Primitives.xmi#SQLDate_1":"RDBSchema:SQLDate";
		}
		if ("long".equals(javaTypeName) || "Long".equals(javaTypeName) || "java.lang.Long".equals(javaTypeName)) {
			return href?"SQL92_Primitives.xmi#SQLNumeric_1":"RDBSchema:SQLNumeric";
		}		
		if ("double".equals(javaTypeName) || "Double".equals(javaTypeName) || "java.math.Double".equals(javaTypeName)) {
			return href?"SQL92_Primitives.xmi#SQLApproximateNumeric_2":"RDBSchema:SQLApproximateNumeric";
		}
		if ("float".equals(javaTypeName) || "Float".equals(javaTypeName) || "java.lang.Float".equals(javaTypeName)) {
			return href?"SQL92_Primitives.xmi#SQLFloat_1":"RDBSchema:SQLFloat";
		}
		if ("short".equals(javaTypeName) || "Short".equals(javaTypeName) || "java.lang.Short".equals(javaTypeName)) {
			return href?"SQL92_Primitives.xmi#SQLExactNumeric_2":"RDBSchema:SQLExactNumeric";
		}
		if ("java.sql.Time".equals(javaTypeName)) {
			return href?"SQL92_Primitives.xmi#SQLTime_1":"RDBSchema:SQLTime";
		}
		if ("java.sql.Timestamp".equals(javaTypeName)) {
			return href?"SQL92_Primitives.xmi#SQLTimestamp_1":"RDBSchema:SQLTimestamp";
		}
		if ("[B".equals(javaTypeName) || "byte[]".equals(javaTypeName) || "byte []".equals(javaTypeName)) {
			return href?"SQL99_Primitives.xmi#SQLBinaryLargeObject_1":"RDBSchema:SQLBinaryLargeObject";
		}
		throw new XavaException("websphere_type_not_supported", javaTypeName);
	}
	
	/**
	 * @return of Locale
	 */
	public static Collection getAvailableLocales(String resourcesFilesPath) {
		if (availableLocales == null) { 
			availableLocales = new HashSet();
			if (XavaPreferences.getInstance().hasPortletLocales()) {			
				for (String locale: XavaPreferences.getInstance().getPortletLocales().split(",")) {
					availableLocales.add(new Locale(locale.trim()));
				}		
			}
			else {
				File dir = new File(resourcesFilesPath);
				String [] files = dir.list();
				for (int i = 0; i < files.length; i++) {
					int idx = files[i].lastIndexOf('_');
					if (idx >= 0) {
						availableLocales.add(new Locale(files[i].substring(idx + 1, idx + 3)));
					}
				}
			}
		}
		return availableLocales;		
	}
	
	public static String convertPropertyNameInPropertyCall(String propertyName) {
		StringTokenizer st = new StringTokenizer(propertyName, ".");
		StringBuffer result = new StringBuffer("get");
		while (st.hasMoreTokens()) {
			result.append(Strings.firstUpper(st.nextToken()));
			if (st.hasMoreTokens()) result.append("().get");
			else result.append("()");
		}
		return result.toString();
	}
	
}
