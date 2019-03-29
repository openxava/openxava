package org.openxava.calculators;

import java.sql.*;



import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.mapping.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class NextIntegerCalculator implements IJDBCCalculator {
	
	private static Log log = LogFactory.getLog(NextIntegerCalculator.class);
	
	private String packageName;
	private IConnectionProvider provider;
	private String model;
	private String property;
	

	private String select;

	public void setConnectionProvider(IConnectionProvider provider) {
		this.provider = provider;
	}

	public Object calculate() throws Exception {
		return new Integer(calculateNextInteger());
	}

	public int calculateNextInteger()
		throws Exception {
		if (provider == null) {
			Object r = Server.calculate(this, getPackageName());			
			if (r instanceof Number) {
				return ((Number) r).intValue();
			}
			return 0;
		}
		Connection con = provider.getConnection();
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(getSelect());			
			rs = ps.executeQuery();
			int nr = 0;
			if (rs.next()) {
				nr = rs.getInt(1);
			}
			ps.close();
			return nr+1;
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("next_integer_calculator_warning"),ex);
			return 1;
		}
		finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception ex) {
				log.error(XavaResources.getString("close_statement_warning"), ex);
			}
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				log.error(XavaResources.getString("close_resultset_warning"), ex);
			}
			con.close();
		}
	}
	
	private String getPackageName() throws XavaException {		
		if (packageName == null) {
			packageName = MetaModel.get(getModel()).getMetaComponent().getPackageNameWithSlashWithoutModel();			
		}		
		return packageName;
	}

	private String getSelect() throws XavaException {
		if (select == null) {
			if (Is.emptyString(this.model, this.property)) {
				throw new XavaException("next_integer_calculator_required_properties");
			}
			ModelMapping mapping = null;
			try {
				mapping = MetaComponent.get(this.model).getEntityMapping();
			}
			catch (ElementNotFoundException ex) {
				mapping = MetaAggregate.get(this.model).getMapping(); 
			}
			String column = mapping.getColumn(this.property);
			String table = mapping.getTable();
			StringBuffer sb = new StringBuffer("select max(");
			sb.append(column);
			sb.append(") from ");
			sb.append(table);
			select = sb.toString();
		}
		return select;		
	}				

	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
		this.select = null;		
	}

	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
		this.select = null;
	}

}
