package org.openxava.types;

import java.io.*;
import java.sql.*;

import org.apache.commons.logging.*;
import org.hibernate.HibernateException;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.*;
import org.openxava.util.*;

/**
 * Supports Blob (and also other types for byte []) in as column type in DB. <p>
 *
 * @author Javier Paniza
 * @since 8.0
 */

public class StringArrayBytesType implements UserType<String> {

	private static Log log = LogFactory.getLog(StringArrayBytesType.class);

	@Override
	public int getSqlType() {
		return SqlTypes.VARBINARY;
	}

	@Override
	public Class<String> returnedClass() {
		return String.class;
	}

	@Override
	public boolean equals(String x, String y) {
		if (x == y) return true;
		if (x == null || y == null) return false;
		return x.equals(y);
	}

	@Override
	public int hashCode(String x) {
		return x == null ? 0 : x.hashCode();
	}

	@Override
	public String nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
		Object o = rs.getObject(position);
    	if (o == null) return "";
    	try {
    		byte[] b = null;
	    	if (o instanceof Blob) {
	    		Blob blob = (Blob) o;
	    		b = blob.getBytes(1l, (int)blob.length());
	    	}
	    	else if (o instanceof byte[]) {
	    		b = (byte[]) o;
			}
	    	else {
	    		throw new HibernateException("conversion_java_byte_array_expected");
	    	}
			return new String(b);
		}
		catch (Exception e){
			log.error(XavaResources.getString("byte_array_to_string_warning"), e);
			return "";
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement st, String value, int index, WrapperOptions options) throws SQLException {
		byte [] bytes = value==null?null:value.toString().getBytes();
		if (log.isTraceEnabled()) {
			log.trace( "binding '" + bytes + "' to parameter: " + index );
		}
		st.setBytes(index, bytes);
	}

	@Override
	public String deepCopy(String value) {
		return value; // String is immutable
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
