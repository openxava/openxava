package org.openxava.types;

import java.io.*;
import java.sql.*;

import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.*;

/**
 * Stores a boolean value as 'S' or 'N' in database. <p>
 *
 * @author Javier Paniza
 * @since 8.0
 */

public class SiNoType implements UserType<Boolean> {

	public static final SiNoType INSTANCE = new SiNoType();

	@Override
	public int getSqlType() {
		return SqlTypes.CHAR;
	}

	@Override
	public Class<Boolean> returnedClass() {
		return Boolean.class;
	}

	@Override
	public boolean equals(Boolean x, Boolean y) {
		if (x == y) return true;
		if (x == null || y == null) return false;
		return x.equals(y);
	}

	@Override
	public int hashCode(Boolean x) {
		return x == null ? 0 : x.hashCode();
	}

	@Override
	public Boolean nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
		String value = rs.getString(position);
		if (value == null) return null;
		return "S".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Boolean value, int index, WrapperOptions options) throws SQLException {
		if (value == null) {
			st.setNull(index, SqlTypes.CHAR);
		} else {
			st.setString(index, value ? "S" : "N");
		}
	}

	@Override
	public Boolean deepCopy(Boolean value) {
		return value; // Boolean is immutable
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Boolean value) {
		return value;
	}

	@Override
	public Boolean assemble(Serializable cached, Object owner) {
		return (Boolean) cached;
	}

	@Override
	public Boolean replace(Boolean original, Boolean target, Object owner) {
		return original;
	}

}
