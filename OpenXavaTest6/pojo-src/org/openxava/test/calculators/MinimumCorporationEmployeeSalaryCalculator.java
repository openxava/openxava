package org.openxava.test.calculators;

import java.sql.*;

import org.openxava.calculators.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class MinimumCorporationEmployeeSalaryCalculator implements IJDBCCalculator {
	
	private IConnectionProvider provider;

	public Object calculate() throws Exception {
		Connection con = provider.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(
				"select min(SALARY) from XAVATEST.CorporationEmployee");
			ResultSet rs = ps.executeQuery();
			rs.next();
			Integer result = new Integer(rs.getInt(1));
			ps.close();
			return result;
		}
		finally {
			con.close();
		}
	}

	public void setConnectionProvider(IConnectionProvider provider) {
		this.provider = provider;
	}

}
