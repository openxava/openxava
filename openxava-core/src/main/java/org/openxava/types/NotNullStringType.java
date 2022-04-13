package org.openxava.types;

import java.io.*;
import java.sql.*;

import org.apache.commons.logging.*;
import org.hibernate.*;
import org.hibernate.engine.spi.*;
import org.hibernate.usertype.*;

/**
 * Allows to remove null for strings on save and on load from database. <p>
 * 
 * @author Javier Paniza
 */

public class NotNullStringType implements UserType {
	
	private static Log log = LogFactory.getLog(NotNullStringType.class);

	public int[] sqlTypes() {		
		return new int[] { Types.VARCHAR };
	}

	public Class returnedClass() {
		return String.class;
	}

	public boolean equals(Object obj1, Object obj2) throws HibernateException {
		if (obj1 == null) return obj2 == null;
		return obj1.equals(obj2);
	}

	public int hashCode(Object obj) throws HibernateException {
		return obj.hashCode();
	}

	public Object nullSafeGet(ResultSet resultSet, String[] names, SharedSessionContractImplementor sessionImplementor, Object owner) throws HibernateException, SQLException { 
		Object o = resultSet.getObject(names[0]);
    	return o == null?"":o;        	    
	}

	public void nullSafeSet(PreparedStatement ps, Object value, int index, SharedSessionContractImplementor sessionImplementor) throws HibernateException, SQLException { 
		ps.setString(index, value==null?"":value.toString());
		
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

}
