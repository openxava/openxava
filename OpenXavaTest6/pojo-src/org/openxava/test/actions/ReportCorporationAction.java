package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.test.model.*;

/**
 *  
 * @author Laurent Wibaux 
 */

public class ReportCorporationAction extends SimpleHTMLReportAction {
	
	protected Map<String, Object> getParameters() 
	throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Corporation company = (Corporation)MapFacade.findEntity(getModelName(), getView().getKeyValuesWithValue());
        parameters.putAll(getEntityParameters(company));
        parameters.put("employees", getCollectionParametersList(company.getEmployees())); 
        return parameters;
	}
	
}
