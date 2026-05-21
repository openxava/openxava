package org.openxava.types;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.hibernate.HibernateException;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;
import org.hibernate.usertype.ParameterizedType;
import org.openxava.util.*;

/**
 * A Java enum that is stored in database using 1 as first value,
 * and 0 as no value. <p>
 * 
 * Useful for compatibity with database created for OX2 valid-values.<br>
 * 
 * @author Javier Paniza
 */
public class Base1EnumType implements UserType<Enum>, ParameterizedType {
	
	private static Log log = LogFactory.getLog(Base1EnumType.class);
	
	private String enumType;

	
	@Override
	public int getSqlType() {
		return SqlTypes.INTEGER;
	}

	@Override
	public Class<Enum> returnedClass() {
		return Enum.class;
	}

	@Override
	public boolean equals(Enum x, Enum y) {
		if (x == y) return true;
		if (x == null || y == null) return false;
		return x.equals(y);
	}

	@Override
	public int hashCode(Enum x) {
		return x == null ? 0 : x.hashCode();
	}

	@Override
	public Enum nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
		Object o = rs.getObject(position);
		if (o == null) return null;
		if (!(o instanceof Number)) {
			throw new HibernateException(XavaResources.getString("conversion_java_number_expected"));
		}

		int idx = ((Number) o).intValue();
		if (idx == 0) return null;
		assertParameters();
		try {
			Object values = Class.forName(enumType).getMethod("values", (Class<?> []) null).invoke(null, (Object []) null);
			return (Enum) ((Object []) values)[idx - 1];
		}
		catch (Exception ex) {
			String message = XavaResources.getString("hibernate_type_enum_error", enumType, getClass());
			log.error(message, ex);
			throw new HibernateException(message);
		}
	}
	
	@Override
	public void nullSafeSet(PreparedStatement st, Enum value, int index, WrapperOptions options) throws SQLException {
		if (value == null) {
			if (log.isTraceEnabled()) {
				log.trace( "binding '0' to parameter: " + index );
			}
			st.setInt(index, 0);
			return;
		}
		if (!(value instanceof Enum)) {
			throw new HibernateException(XavaResources.getString("conversion_db_enum_expected"));
		}
		int ivalue = value.ordinal() + 1;
		if (log.isTraceEnabled()) {
			log.trace( "binding '" + ivalue + "' to parameter: " + index );
		}
		st.setInt(index, ivalue);
	}
	
	@Override
	public Enum deepCopy(Enum value) {
		return value; // Enum is immutable
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Enum value) {
		return value;
	}

	@Override
	public Enum assemble(Serializable cached, Object owner) {
		return (Enum) cached;
	}

	@Override
	public Enum replace(Enum original, Enum target, Object owner) {
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
