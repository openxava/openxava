package org.openxava.converters.typeadapters;

import java.io.*;
import java.math.*;
import java.net.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * To extract the object assigned to a PreparedStatementin. <p>
 *
 * The .class is included in ox-jdbc-adapters.jar. <br> 
 *   
 * @author Javier Paniza
 */

public class ObjectPreparedStatementAdapter implements PreparedStatement {
	
	private Object object;
	
	/**
	 * The object assigned to this PreparedStatemet using a set* method.
	 */
	public Object getObject() {
		return object;
	}

	public ResultSet executeQuery() throws SQLException {
		return null;
	}

	public int executeUpdate() throws SQLException {
		return 1;
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		object = null;
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		object = Boolean.valueOf(x);
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		object = new Byte(x);
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		object = new Short(x);
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		object = new Integer(x);
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		object = new Long(x);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		object = new Float(x);
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		object = new Double(x);
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x)	throws SQLException {
		object = x;
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		object = x;
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		object = x;
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		object = x;
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		object = x;
	}

	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		object = x;
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		object = x;
	}

	public void setUnicodeStream(int parameterIndex, InputStream x, int length)	throws SQLException {
		object = x;
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		object = x;
	}

	public void clearParameters() throws SQLException {
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
		object = x;
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		object = x;
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		object = x;
	}

	public boolean execute() throws SQLException {
		return false;
	}

	public void addBatch() throws SQLException {
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
	}

	public void setRef(int i, Ref x) throws SQLException {
		object = x;
	}

	public void setBlob(int i, Blob x) throws SQLException {
		object = x;
	}

	public void setClob(int i, Clob x) throws SQLException {
		object = x;
	}

	public void setArray(int i, Array x) throws SQLException {
		object = x;
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return null;
	}

	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		object = x;
	}

	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		object = x;
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		object = x;
	}

	public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
		object = null;
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		object = x;
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {	
		return null;
	}

	public ResultSet executeQuery(String sql) throws SQLException {		
		return null;
	}

	public int executeUpdate(String sql) throws SQLException {
		return 1;
	}

	public void close() throws SQLException {
	}

	public int getMaxFieldSize() throws SQLException {
		return Integer.MAX_VALUE;
	}

	public void setMaxFieldSize(int max) throws SQLException {	
	}

	public int getMaxRows() throws SQLException {
		return 1;
	}

	public void setMaxRows(int max) throws SQLException {
	}

	public void setEscapeProcessing(boolean enable) throws SQLException {
	}

	public int getQueryTimeout() throws SQLException {
		return 0;
	}

	public void setQueryTimeout(int seconds) throws SQLException {
	}

	public void cancel() throws SQLException {
	}

	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	public void clearWarnings() throws SQLException {
	}

	public void setCursorName(String name) throws SQLException {
	}

	public boolean execute(String sql) throws SQLException {
		return false;
	}

	public ResultSet getResultSet() throws SQLException {
		return null;
	}

	public int getUpdateCount() throws SQLException {
		return 0;
	}

	public boolean getMoreResults() throws SQLException {
		return false;
	}

	public void setFetchDirection(int direction) throws SQLException {
	}

	public int getFetchDirection() throws SQLException {
		return 0;
	}

	public void setFetchSize(int rows) throws SQLException {
	}

	public int getFetchSize() throws SQLException {
		return 0;
	}

	public int getResultSetConcurrency() throws SQLException {
		return 0;
	}

	public int getResultSetType() throws SQLException {
		return 0;
	}

	public void addBatch(String sql) throws SQLException {
	}

	public void clearBatch() throws SQLException {
	}

	public int[] executeBatch() throws SQLException {
		return null;
	}

	public Connection getConnection() throws SQLException {
		return null;
	}

	public boolean getMoreResults(int current) throws SQLException {
		return false;
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		return null;
	}

	public int executeUpdate(String sql, int autoGeneratedKeys)	throws SQLException {
		return 0;
	}

	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return 0;
	}

	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return 0;
	}

	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return false;
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return false;
	}

	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return false;
	}

	public int getResultSetHoldability() throws SQLException {
		return 0;
	}

}
