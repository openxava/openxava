package org.openxava.types;

import java.io.*;
import java.sql.*;

import org.apache.commons.logging.*;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.*;

/**
 * Allows to remove null for strings on save and on load from database. <p>
 * 
 * @author Javier Paniza
 */

public class NotNullStringType implements UserType<String> {

	private static Log log = LogFactory.getLog(NotNullStringType.class);

	@Override
	public int getSqlType() {
		return SqlTypes.VARCHAR;
	}

	@Override
	public Class<String> returnedClass() {
		return String.class;
	}

	@Override
	public boolean equals(String obj1, String obj2) {
		if (obj1 == null) return obj2 == null;
		return obj1.equals(obj2);
	}

	@Override
	public int hashCode(String obj) {
		return obj == null ? 0 : obj.hashCode();
	}

	@Override
	public String nullSafeGet(ResultSet resultSet, int position, WrapperOptions options) throws SQLException {
		String value = resultSet.getString(position);
		return value == null ? "" : value;
	}

	@Override
	public void nullSafeSet(PreparedStatement ps, String value, int index, WrapperOptions options) throws SQLException {
		ps.setString(index, value == null ? "" : value);
	}

	@Override
	public String deepCopy(String value) {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(String value) {
		return value;
	}

	@Override
	public String assemble(Serializable cached, Object owner) {
		return (String) cached;
	}

	@Override
	public String replace(String original, String target, Object owner) {
		return original;
	}

}
