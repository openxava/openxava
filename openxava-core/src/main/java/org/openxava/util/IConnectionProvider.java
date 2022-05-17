package org.openxava.util;

import java.sql.*;

/**
 * Provides JDBC connections. <p>
 *
 * @author  Javier Paniza
 */

public interface IConnectionProvider {

  /**
   * Returns a JDBC connection by default. <p>
   * 
   * @exception SQLException  If there are problem obtaining the connetion
   */
  Connection getConnection() throws SQLException;
  
  /**
   * Returns a JDBC connection from a identifier. <p>
   * 
   * @param dataSourceName  Name of data source from what I obtain the connection
   * @exception SQLException  If there are problem obtaining the connetion
   */
  Connection getConnection(String dataSourceName) throws SQLException;
  
	/**
	 * Sets password used to create connection. <p>	 * 
	 *
	 * Must to call to {@link #setUser} too. Although set user and password
	 * is not mandatory.<br>
	 */
	void setPassword(String password);
	
  /**
   * Sets the datasource name used when using {@link #getConnection}. <br>
   */
  void setDefaultDataSource(String dataSourceName);
  
	/**
	 * Sets the user used to craete the connection. <p> 
	 *
	 * It is not mandatory to call this method.<br>
	 */
  void setUser(String user);
  
}
