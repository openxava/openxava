package org.openxava.types;

import java.io.*;
import java.sql.*;

import org.hibernate.dialect.*;
import org.hibernate.engine.spi.*;
import org.hibernate.type.*;
import org.hibernate.type.descriptor.java.BooleanJavaType;
import org.hibernate.type.descriptor.jdbc.CharJdbcType;

/**
 * Stores a boolean value as 'S' or 'N' in database. <p>
 * 
 * @author Javier Paniza
 */

public class SiNoType 
	extends AbstractSingleColumnStandardBasicType<Boolean>
{
	
	public static final SiNoType INSTANCE = new SiNoType();

	public SiNoType() {
		super( CharJdbcType.INSTANCE, new BooleanJavaType('S', 'N') );
	}

	public String getName() {
		return "yes_no";
	}

	public Class getPrimitiveClass() {
		return boolean.class;
	}
	
	public Boolean stringToObject(String xml) throws Exception {
		return fromString( xml );
	}

	public Serializable getDefaultValue() {
		return Boolean.FALSE;
	}

	@SuppressWarnings({ "UnnecessaryUnboxing" })
	public String objectToSQLString(Boolean value, Dialect dialect) throws Exception {
		return "'" + (value.booleanValue() ? "S" : "N") + "'";
	}
	
}
