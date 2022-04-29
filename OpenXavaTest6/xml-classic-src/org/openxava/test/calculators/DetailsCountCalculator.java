package org.openxava.test.calculators;

import java.sql.*;

import org.apache.commons.logging.*;
import org.openxava.calculators.*;
import org.openxava.component.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class DetailsCountCalculator implements IJDBCCalculator {
	
	private IConnectionProvider provider;
	private int year;
	private int number;
	
	public void setConnectionProvider(IConnectionProvider proveedor) {
		this.provider = proveedor;
	}

	public Object calculate() throws Exception {
		Connection con = provider.getConnection();
		try {		
			String table = MetaComponent.get("Invoice").getAggregateMapping("InvoiceDetail").getTable();
			PreparedStatement ps = con.prepareStatement("select count(*) from " + table + " where INVOICE_YEAR = ? and INVOICE_NUMBER = ?");						
			ps.setInt(1, getYear());
			ps.setInt(2, getNumber());
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

	public int getYear() {
		return year;
	}

	public int getNumber() {
		return number;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setNumber(int numero) {
		this.number = numero;
	}

}
