package org.openxava.test.types;

import java.io.*;
import java.sql.*;

import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.*;
import org.openxava.util.*;

/**
 *
 * @author Javier Paniza
 * @since 8.0
 */

public class RegionsType implements UserType<String[]> {

	@Override
	public int getSqlType() {
		return SqlTypes.VARCHAR;
	}

	@Override
	public Class<String[]> returnedClass() {
		return String[].class;
	}

	@Override
	public boolean equals(String[] x, String[] y) {
		return Is.equal(x, y);
	}

	@Override
	public int hashCode(String[] x) {
		return x == null ? 0 : x.hashCode();
	}

	@Override
	public String[] nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
		Object o = rs.getObject(position);
   		if (o == null) return new String[0];
   		String dbValue = (String) o;
   		String [] javaValue = new String [dbValue.length()];
   		for (int i = 0; i < javaValue.length; i++) {
   			javaValue[i] = String.valueOf(dbValue.charAt(i));
   		}
   		return javaValue;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, String[] value, int index, WrapperOptions options) throws SQLException {
		if (value == null) {
			st.setString(index, "");
			return;
		}
		StringBuffer dbValue = new StringBuffer();
		for (int i = 0; i < value.length; i++) {
			dbValue.append(value[i]);
		}
		st.setString(index, dbValue.toString());
	}

	@Override
	public String[] deepCopy(String[] value) {
		return value == null ? null : value.clone();
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Serializable disassemble(String[] value) {
		return value;
	}

	@Override
	public String[] assemble(Serializable cached, Object owner) {
		return (String[]) cached;
	}

	@Override
	public String[] replace(String[] original, String[] target, Object owner) {
		return original;
	}

}
