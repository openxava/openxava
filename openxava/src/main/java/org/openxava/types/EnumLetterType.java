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
 * In java an Enum and in database a letter corresponding
 * to the position in the string that it's in property 'letters'. <p>
 *
 * @author Javier Paniza
 * @since 8.0
 */

public class EnumLetterType implements UserType<Enum>, ParameterizedType {

	private static Log log = LogFactory.getLog(EnumLetterType.class);

	private String letters;
	private String enumType;

	@Override
	public int getSqlType() {
		return SqlTypes.VARCHAR;
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
		if (!(o instanceof String)) {
			throw new HibernateException(XavaResources.getString("conversion_java_string_expected"));
		}
		assertParameters();
		String value  = (String) o;
		if (Is.emptyString(value)) return null;
		int idx = letters.indexOf(value);
		if (idx < 0) {
			throw new HibernateException(XavaResources.getString("conversion_java_valid_values", value,  letters));
		}
		try {
			Object values = Class.forName(enumType).getMethod("values", (Class<?> []) null).invoke(null, (Object []) null);
			return (Enum) ((Object []) values)[idx];
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
				log.trace( "binding '' to parameter: " + index );
			}
			st.setString(index, "");
			return;
		}
		if (!(value instanceof Enum)) {
			throw new HibernateException(XavaResources.getString("conversion_db_enum_expected"));
		}
		assertParameters();
		int ivalue = ((Enum) value).ordinal();
		try {
			String letter = String.valueOf(letters.charAt(ivalue));
			if (log.isTraceEnabled()) {
				log.trace( "binding '" + letter + "' to parameter: " + index );
			}
			st.setString(index, letter);
		}
		catch (IndexOutOfBoundsException ex) {
			throw new HibernateException(XavaResources.getString("conversion_db_valid_values", value, letters));
		}
	}

	@Override
	public void setParameterValues(Properties parameters) {
		if (parameters == null) return;
		letters = parameters.getProperty("letters");
		enumType = parameters.getProperty("enumType");
	}

	private void assertParameters() throws HibernateException {
		if (Is.emptyString(letters)) {
			throw new HibernateException(XavaResources.getString("conversion_valid_values_letters_required", getClass().getName()));
		}
		if (Is.emptyString(enumType)) {
			throw new HibernateException(XavaResources.getString("hibernate_type_parameter_required", "enumType", getClass().getName()));
		}
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

	/**
	 * Full qualified class for the Enum. <p>
	 *
	 * For example: "org.openxava.test.model.Delivery$Distance"
	 */
	public String getEnumType() {
		return enumType;
	}
	/**
	 * Full qualified class for the Enum. <p>
	 *
	 * For example: "org.openxava.test.model.Delivery$Distance"
	 */
	public void setEnumType(String enumType) {
		this.enumType = enumType;
	}

	/**
	 * Letters string that corresponds with the valid values for this enum. <p>
	 *
	 * For example, "AEI", means:
	 * <ul>
	 * <li> ordinal 0 in Java Enum for 'A' in DB
	 * <li> ordinal 1 in Java Enum for 'E' in DB
	 * <li> ordinal 2 in Java Enum for 'I' in DB
	 * <li>
	 * </ul>
	 */
	public String getLetters() {
		return letters;
	}
	/**
	 * Letters string that corresponds with the valid values for this enum. <p>
	 *
	 * For example, "AEI", means:
	 * <ul>
	 * <li> ordinal 0 in Java Enum for 'A' in DB
	 * <li> ordinal 1 in Java Enum for 'E' in DB
	 * <li> ordinal 2 in Java Enum for 'I' in DB
	 * <li>
	 * </ul>
	 */
	public void setLetters(String letters) {
		this.letters = letters;
	}

}
