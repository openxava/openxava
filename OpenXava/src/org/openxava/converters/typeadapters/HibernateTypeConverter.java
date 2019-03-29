package org.openxava.converters.typeadapters;

import java.sql.*;

import org.apache.commons.logging.*;
import org.hibernate.type.*;
import org.hibernate.usertype.*;
import org.openxava.converters.*;
import org.openxava.util.*;

/**
 * Adapter for using Hibernate types as converters in OpenXava. <p>
 * 
 * It works with <code>org.hibernate.usertype.UserType</code> and 
 * <code>org.hibernate.type.Type</code>.<br>
 * 
 * @author Javier Paniza
 */

public class HibernateTypeConverter extends HibernateTypeBaseConverter implements IConverter {
	
	private static Log log = LogFactory.getLog(HibernateTypeConverter.class);
	private final static String [] fields = {"f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "f10" }; // For avoiding NullPointer with some hibernate types
		
	public Object toJava(Object o) throws ConversionException {
		try {		
			Object hibernateType = getHibernateType();
			Object result = null;
			if (hibernateType instanceof Type) {
				result = ((Type) hibernateType).nullSafeGet(new ArrayOneRowResultSetAdapter(new Object[] { o }), "f1", null, null);
			}
			else if (hibernateType instanceof UserType) {
				result = ((UserType) hibernateType).nullSafeGet(new ArrayOneRowResultSetAdapter(new Object[] { o }), fields, null, null); 
				
			}
			else {
				throw new ConversionException("only_type_and_usertype", hibernateType.getClass());
			}
			if (result instanceof Enum) {
				// The EntityTab works with numbers (that converts back to Enums)
				return ((Enum) result).ordinal();
			}							
			return result;
		} 
		catch (Exception ex) {
			log.error(XavaResources.getString("hibernate_type_conversion_error", getType()), ex);
			throw new ConversionException(ex.getMessage()); 
		}
	}

	public Object toDB(Object o) throws ConversionException { 
		ObjectPreparedStatementAdapter ps = null;
		try {		
			Object hibernateType = getHibernateType();
			Object result = null;
			if (hibernateType instanceof Type) {
				ps = new ObjectPreparedStatementAdapter();
				((Type) hibernateType).nullSafeSet(ps, o, 1, null);
				result = ps.getObject();
			}
			else if (hibernateType instanceof UserType) {
				ps = new ObjectPreparedStatementAdapter();
				((UserType) hibernateType).nullSafeSet(ps, o, 1, null); 
				result = ps.getObject();
			}
			else {
				throw new ConversionException("only_type_and_usertype", hibernateType.getClass()); 
			}
			if (result instanceof Enum) {
				// The EntityTab works with numbers
				result = ((Enum) result).ordinal();
			}				
			return result;			
		} 
		catch (Exception ex) {
			log.error(XavaResources.getString("hibernate_type_conversion_error", getType()), ex);
			throw new ConversionException(ex.getMessage()); 
		} finally {
			try {
				ps.close();
			} catch (Exception ex) {
				log.warn(XavaResources.getString("close_statement_warning"));
			}
		}
	}
	
}
