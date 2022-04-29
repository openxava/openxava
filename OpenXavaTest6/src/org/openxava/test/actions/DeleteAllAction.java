package org.openxava.test.actions;

import java.sql.*;

import org.openxava.actions.*;
import org.openxava.component.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class DeleteAllAction extends BaseAction implements IJDBCAction {

	private IConnectionProvider provider;
	private String model;

	public void execute() throws Exception {
		Connection con = provider.getConnection();
		try {
			
			String table = MetaComponent.get(getModel()).getEntityMapping().getTable();
			PreparedStatement ps = con.prepareStatement("DELETE FROM " + table);
			ps.executeUpdate();
			ps.close();
		}
		finally {
			con.close();			
		}
	}

	public void setConnectionProvider(IConnectionProvider provider) {
		this.provider = provider;		
	}

	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	
}
