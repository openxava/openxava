package org.openxava.test.types;

import java.io.*;
import java.sql.*;

import org.hibernate.*;
import org.hibernate.engine.spi.*;
import org.hibernate.usertype.*;
import org.openxava.util.*;

/** 
 * 
 * @author Javier Paniza
 */

public class RegionsType implements UserType {

	public int[] sqlTypes() {		
		return new int[] { Types.VARCHAR };
	}

	public Class returnedClass() {
		return String[].class;
	}

	public boolean equals(Object obj1, Object obj2) throws HibernateException {
		return Is.equal(obj1, obj2);
	}

	public int hashCode(Object obj) throws HibernateException {
		return obj.hashCode();
	}

	public Object nullSafeGet(ResultSet resultSet, String[] names, SharedSessionContractImplementor implementor, Object owner) throws HibernateException, SQLException { 
		Object o = resultSet.getObject(names[0]);
   		if (o == null) return new String[0];
   		String dbValue = (String) o; 
   		String [] javaValue = new String [dbValue.length()];
   		for (int i = 0; i < javaValue.length; i++) {
   			javaValue[i] = String.valueOf(dbValue.charAt(i));			
   		}
   		return javaValue;
	}

	public void nullSafeSet(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor implementor) throws HibernateException, SQLException {
		if (value == null) {
			ps.setString(index, "");
			return;
		}
		String [] javaValue = (String []) value;
		StringBuffer dbValue = new StringBuffer();
		for (int i = 0; i < javaValue.length; i++) {
			dbValue.append(javaValue[i]);
		}
		ps.setString(index, dbValue.toString());		
	}

	public Object deepCopy(Object obj) throws HibernateException {		
		return obj == null?null:((String []) obj).clone();
	}

	public boolean isMutable() {
		return true;
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

}
