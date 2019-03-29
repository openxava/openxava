package org.openxava.converters.typeadapters;

import java.io.*;
import java.math.*;
import java.net.*;
import java.sql.*;
import java.util.*;

/** 
 * Exposes the data in an array of byte as a JDBC ResultSet of one unique row. <p>
 * 
 * Each object in the array represents a column in the ResultSet, 
 * and the full array represents a row.<br>
 * 
 * At the moment only works in a read-only way. <br>
 *
 * The .class is included in ox-jdbc-adapters.jar. <br>
 *  
 * @author Javier Paniza
 */
public class ArrayOneRowResultSetAdapter implements ResultSet { 
	
	private Object [] data;
	
	/**
	 * 
	 * @param data  The data to wrap
	 */
	public ArrayOneRowResultSetAdapter(Object [] data) {
		this.data = data;			
	}

	public boolean next() throws SQLException {
		return false;
	}

	public void close() throws SQLException {
	}

	public boolean wasNull() throws SQLException {
		return false;
	}

	public String getString(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		return value == null?null:value.toString();		
	}

	public boolean getBoolean(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof Boolean) return ((Boolean) value).booleanValue(); 
		return false;
	}

	public byte getByte(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof Number) return ((Number) value).byteValue();
		return 0;
	}

	public short getShort(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof Number) return ((Number) value).shortValue();
		return 0;
	}

	public int getInt(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof Number) return ((Number) value).intValue();
		return 0;
	}

	public long getLong(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof Number) return ((Number) value).longValue();
		return 0;
	}

	public float getFloat(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof Number) return ((Number) value).floatValue();
		return 0;
	}

	public double getDouble(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof Number) return ((Number) value).doubleValue();
		return 0;
	}

	public BigDecimal getBigDecimal(int columnIndex, int scale)	throws SQLException {
		BigDecimal result = getBigDecimal(0);
		return result==null?null:result.setScale(scale);
	}

	public byte[] getBytes(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof byte[]) return (byte[]) value;
		return null;
	}

	public java.sql.Date getDate(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof java.sql.Date) return (java.sql.Date) value;
		return null;
	}

	public Time getTime(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof Time) return (Time) value;
		return null;
	}

	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof Timestamp) return (Timestamp) value;
		return null;
	}

	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof InputStream) return (InputStream) value;
		return null;
	}

	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof InputStream) return (InputStream) value;
		return null;
	}

	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof InputStream) return (InputStream) value;
		return null;
	}

	public String getString(String columnName) throws SQLException {
		Object value = getObject(columnName);
		return value == null?null:value.toString();			
	}

	public boolean getBoolean(String columnName) throws SQLException {
		Object value = getObject(columnName);
		if (value instanceof Boolean) return ((Boolean) value).booleanValue(); 
		return false;			
	}

	public byte getByte(String columnName) throws SQLException {		
		Object value = getObject(columnName);
		if (value instanceof Number) return ((Number) value).byteValue();
		return 0;
	}

	public short getShort(String columnName) throws SQLException {		
		Object value = getObject(columnName);
		if (value instanceof Number) return ((Number) value).shortValue();
		return 0;
	}

	public int getInt(String columnName) throws SQLException {		
		Object value = getObject(columnName);
		if (value instanceof Number) return ((Number) value).intValue();
		return 0;
	}

	public long getLong(String columnName) throws SQLException {		
		Object value = getObject(columnName);
		if (value instanceof Number) return ((Number) value).longValue();
		return 0;
	}

	public float getFloat(String columnName) throws SQLException {		
		Object value = getObject(columnName);
		if (value instanceof Number) return ((Number) value).floatValue();
		return 0;
	}

	public double getDouble(String columnName) throws SQLException {		
		Object value = getObject(columnName);
		if (value instanceof Number) return ((Number) value).doubleValue();
		return 0;
	}

	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
		BigDecimal result = getBigDecimal(columnName);
		return result==null?null:result.setScale(scale);
	}

	public byte[] getBytes(String columnName) throws SQLException {		
		Object value = getObject(columnName);
		if (value instanceof byte[]) return (byte[]) value;
		return null;
	}

	public java.sql.Date getDate(String columnName) throws SQLException {		
		Object value = getObject(columnName);
		if (value instanceof java.sql.Date) return (java.sql.Date) value;
		return null;
	}

	public Time getTime(String columnName) throws SQLException {
		Object value = getObject(columnName);
		if (value instanceof Time) return (Time) value;
		return null;
	}

	public Timestamp getTimestamp(String columnName) throws SQLException {		
		Object value = getObject(columnName);
		if (value instanceof Timestamp) return (Timestamp) value;
		return null;
	}

	public InputStream getAsciiStream(String columnName) throws SQLException {		
		Object value = getObject(columnName);
		if (value instanceof InputStream) return (InputStream) value;
		return null;
	}

	public InputStream getUnicodeStream(String columnName) throws SQLException {
		Object value = getObject(columnName);
		if (value instanceof InputStream) return (InputStream) value;
		return null;
	}

	public InputStream getBinaryStream(String columnName) throws SQLException {
		Object value = getObject(columnName);
		if (value instanceof InputStream) return (InputStream) value;
		return null;
	}

	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	public void clearWarnings() throws SQLException {
	}

	public String getCursorName() throws SQLException {
		return null;
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return null;
	}

	public Object getObject(int columnIndex) throws SQLException {			
		return data[columnIndex-1];
	}

	public Object getObject(String columnName) throws SQLException {			
		return getObject(findColumn(columnName));
	}

	public int findColumn(String columnName) throws SQLException {
		return Integer.parseInt(columnName.substring(1));
	}

	public Reader getCharacterStream(int columnIndex) throws SQLException {		
		return null;
	}

	public Reader getCharacterStream(String columnName) throws SQLException {
		return null;
	}

	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof BigDecimal) return ((BigDecimal) value);
		if (value instanceof Number) return new BigDecimal(((Number) value).doubleValue());
		return null;
	}

	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		Object value = getObject(columnName);
		if (value instanceof BigDecimal) return ((BigDecimal) value);
		if (value instanceof Number) return new BigDecimal(((Number) value).doubleValue());
		return null;			
	}

	public boolean isBeforeFirst() throws SQLException {
		return false;
	}

	public boolean isAfterLast() throws SQLException {
		return false;
	}

	public boolean isFirst() throws SQLException {
		return false;
	}

	public boolean isLast() throws SQLException {
		return false;
	}

	public void beforeFirst() throws SQLException {

	}

	public void afterLast() throws SQLException {
	}

	public boolean first() throws SQLException {
		return false;
	}

	public boolean last() throws SQLException {
		return false;
	}

	public int getRow() throws SQLException {
		return 0;
	}

	public boolean absolute(int row) throws SQLException {
		return false;
	}

	public boolean relative(int rows) throws SQLException {
		return false;
	}

	public boolean previous() throws SQLException {
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

	public int getType() throws SQLException {
		return 0;
	}

	public int getConcurrency() throws SQLException {
		return 0;
	}

	public boolean rowUpdated() throws SQLException {
		return false;
	}

	public boolean rowInserted() throws SQLException {
		return false;
	}

	public boolean rowDeleted() throws SQLException {
		return false;
	}

	public void updateNull(int columnIndex) throws SQLException {
	}

	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
	}

	public void updateByte(int columnIndex, byte x) throws SQLException {
	}

	public void updateShort(int columnIndex, short x) throws SQLException {
	}

	public void updateInt(int columnIndex, int x) throws SQLException {
	}

	public void updateLong(int columnIndex, long x) throws SQLException {
	}

	public void updateFloat(int columnIndex, float x) throws SQLException {
	}

	public void updateDouble(int columnIndex, double x) throws SQLException {
	}

	public void updateBigDecimal(int columnIndex, BigDecimal x)	throws SQLException {
	}

	public void updateString(int columnIndex, String x) throws SQLException {
	}

	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
	}

	public void updateDate(int columnIndex, java.sql.Date x) throws SQLException {
	}

	public void updateTime(int columnIndex, Time x) throws SQLException {
	}

	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
	}

	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
	}

	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
	}

	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
	}

	public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
	}

	public void updateObject(int columnIndex, Object x) throws SQLException {
	}

	public void updateNull(String columnName) throws SQLException {
	}

	public void updateBoolean(String columnName, boolean x) throws SQLException {
	}

	public void updateByte(String columnName, byte x) throws SQLException {
	}

	public void updateShort(String columnName, short x) throws SQLException {
	}

	public void updateInt(String columnName, int x) throws SQLException {
	}

	public void updateLong(String columnName, long x) throws SQLException {
	}

	public void updateFloat(String columnName, float x) throws SQLException {
	}

	public void updateDouble(String columnName, double x) throws SQLException {
	}

	public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
	}

	public void updateString(String columnName, String x) throws SQLException {
	}

	public void updateBytes(String columnName, byte[] x) throws SQLException {
	}

	public void updateDate(String columnName, java.sql.Date x) throws SQLException {
	}

	public void updateTime(String columnName, Time x) throws SQLException {
	}

	public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
	}

	public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
	}

	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
	}

	public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
	}

	public void updateObject(String columnName, Object x, int scale) throws SQLException {
	}

	public void updateObject(String columnName, Object x) throws SQLException {
	}

	public void insertRow() throws SQLException {
	}

	public void updateRow() throws SQLException {
	}

	public void deleteRow() throws SQLException {
	}

	public void refreshRow() throws SQLException {
	}

	public void cancelRowUpdates() throws SQLException {
	}

	public void moveToInsertRow() throws SQLException {
	}

	public void moveToCurrentRow() throws SQLException {
	}

	public Statement getStatement() throws SQLException {
		return null;
	}

	public Object getObject(int i, Map map) throws SQLException {		
		return getObject(i);
	}

	public Ref getRef(int i) throws SQLException {
		return null;
	}

	public Blob getBlob(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof Blob) return (Blob) value;
		return null;
	}

	public Clob getClob(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof Clob) return (Clob) value;
		return null;
	}

	public Array getArray(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof Array) return (Array) value;
		return null;
	}

	public Object getObject(String columnName, Map map) throws SQLException {
		return getObject(columnName);
	}

	public Ref getRef(String colName) throws SQLException {
		return null;
	}

	public Blob getBlob(String columnName) throws SQLException {		
		Object value = getObject(columnName);
		if (value instanceof Blob) return (Blob) value;
		return null;
	}

	public Clob getClob(String columnName) throws SQLException {
		Object value = getObject(columnName);
		if (value instanceof Clob) return (Clob) value;
		return null;
	}

	public Array getArray(String columnName) throws SQLException {
		Object value = getObject(columnName);
		if (value instanceof Array) return (Array) value;
		return null;
	}

	public java.sql.Date getDate(int columnIndex, Calendar cal) throws SQLException {		
		return getDate(columnIndex);
	}

	public java.sql.Date getDate(String columnName, Calendar cal) throws SQLException {
		return getDate(columnName);
	}

	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return getTime(columnIndex);
	}

	public Time getTime(String columnName, Calendar cal) throws SQLException {
		return getTime(columnName);
	}

	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return getTimestamp(columnIndex);
	}

	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
		return getTimestamp(columnName);
	}

	public URL getURL(int columnIndex) throws SQLException {
		Object value = getObject(columnIndex);
		if (value instanceof URL) return (URL) value;
		return null;
	}

	public URL getURL(String columnName) throws SQLException {
		Object value = getObject(columnName);
		if (value instanceof URL) return (URL) value;
		return null;
	}

	public void updateRef(int columnIndex, Ref x) throws SQLException {
	}

	public void updateRef(String columnName, Ref x) throws SQLException {
	}

	public void updateBlob(int columnIndex, Blob x) throws SQLException {
	}

	public void updateBlob(String columnName, Blob x) throws SQLException {
	}

	public void updateClob(int columnIndex, Clob x) throws SQLException {
	}

	public void updateClob(String columnName, Clob x) throws SQLException {
	}

	public void updateArray(int columnIndex, Array x) throws SQLException {
	}

	public void updateArray(String columnName, Array x) throws SQLException {
	}

}

