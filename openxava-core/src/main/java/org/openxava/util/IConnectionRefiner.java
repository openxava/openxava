package org.openxava.util;

import java.sql.*;

/**
 * To refine the JDBC connections just after get them from the data source and before use them. <p>
 * 
 * The implementation class for the application is defined in connectionRefinerClass of xava.properties.
 * 
 * @since 5.6
 * @author Javier Paniza
 */
public interface IConnectionRefiner { 
	
	void refine(Connection con) throws Exception;

}
