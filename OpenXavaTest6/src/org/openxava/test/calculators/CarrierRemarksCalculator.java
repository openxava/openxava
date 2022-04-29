package org.openxava.test.calculators;

import java.sql.*;

import org.openxava.calculators.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class CarrierRemarksCalculator implements IJDBCCalculator {

	private String drivingLicenceType;
	private IConnectionProvider provider; 

	public Object calculate() throws Exception {		
		if (drivingLicenceType == null) return "";
		if (drivingLicenceType.toUpperCase().startsWith("C")) {
			return "He can drive trucks: " + getCarrierCount(); 
		}
		return "";
	}
	public String getDrivingLicenceType() {
		return drivingLicenceType;
	}
	public void setDrivingLicenceType(String tipoDrivingLicence) {
		this.drivingLicenceType = tipoDrivingLicence;
	}
	
	private int getCarrierCount() throws Exception {
		Connection con = provider.getConnection();
		try {
			PreparedStatement pd = con.prepareStatement("SELECT COUNT(*) FROM XAVATEST.CARRIER");
			ResultSet rs = pd.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			rs.close();
			pd.close();
			return count;
		}
		finally {
			con.close();
		}
	}

	public void setConnectionProvider(IConnectionProvider provider) { 
		this.provider = provider;
	}
	
}
