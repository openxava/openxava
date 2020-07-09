package org.openxava.types;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.hibernate.*;
import org.hibernate.engine.spi.*;
import org.hibernate.usertype.*;
import org.openxava.util.*;

/**
 * A Java enum that is stored in database using 1 as first value,
 * and 0 as no value. <p>
 * 
 * Useful for compatibity with database created for OX2 valid-values.<br>
 * 
 * @author Javier Paniza
 */
public class Base1EnumType implements UserType, ParameterizedType {
	
	private static Log log = LogFactory.getLog(EnumLetterType.class);
	
	private String enumType;

	
	public int[] sqlTypes() {		
		return new int[] { Types.INTEGER };
	}

	public Class returnedClass() {
		return Enum.class;		
	}

	public boolean equals(Object obj1, Object obj2) throws HibernateException {
		if (obj1 == obj2) return true;
		if (obj1 == null) return false;
		return obj1.equals(obj2);
	}

	public int hashCode(Object obj) throws HibernateException {
		return obj.hashCode();
	}

	public Object nullSafeGet(ResultSet resultSet, String[] names, SharedSessionContractImplementor sessionImplementor, Object owner) throws HibernateException, SQLException { 	
		Object o = resultSet.getObject(names[0]);
		if (o == null) return null;
		if (!(o instanceof Number)) { 
			throw new HibernateException(XavaResources.getString("conversion_java_number_expected"));
		}

		int idx  = ((Number) o).intValue();
		if (idx == 0) return null;
		assertParameters();
		try {
			Object values = Class.forName(enumType).getMethod("values", (Class<?> []) null).invoke(null, (Object []) null); 
			return ((Object []) values)[idx - 1];
		} 
		catch (Exception ex) {
			String message = XavaResources.getString("hibernate_type_enum_error", enumType, getClass()); 
			log.error(message, ex);
			throw new HibernateException(message);
		}
	}
	
	public void nullSafeSet(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor sessionImplementor) throws HibernateException, SQLException { 
		if (value == null) {
			ps.setInt(index, 0);
			return;
		}
		if (!(value instanceof Enum)) {		
			throw new HibernateException(XavaResources.getString("conversion_db_enum_expected"));
		}	
		int ivalue = ((Enum) value).ordinal() + 1;
		if (log.isTraceEnabled()) {
			log.trace( "binding '" + ivalue + "' to parameter: " + index );
		}
		ps.setInt(index, ivalue);				
	}
	
	public Object deepCopy(Object obj) throws HibernateException {
		return obj;		
	}

	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Object obj) throws HibernateException {
		return (Serializable) obj;
	}

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}
	
	private void assertParameters() throws HibernateException {
		if (Is.emptyString(enumType)) {
			throw new HibernateException(XavaResources.getString("hibernate_type_parameter_required", "enumType", getClass().getName())); 
		}		
	}
	
	public void setParameterValues(Properties parameters) {
		if (parameters == null) return;
		enumType = parameters.getProperty("enumType");
	}



}
