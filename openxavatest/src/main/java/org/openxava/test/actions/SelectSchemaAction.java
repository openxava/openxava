package org.openxava.test.actions;

import java.util.Collection;
import java.util.Iterator;

import javax.inject.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.actions.SetDefaultSchemaAction;
import org.openxava.controller.ModuleManager;
import org.openxava.model.meta.*;
import org.openxava.view.View;

/**
 * Create on 03/09/2009 (14:49:04)
 * @autor Ana Andrés
 */
public class SelectSchemaAction extends SetDefaultSchemaAction {
	
	private static Log log = LogFactory.getLog(SelectSchemaAction.class);
	
	@Inject
	private View view;
	
	public void execute() throws Exception {
		String schema = view.getValueString("schema");
		try {
			int schemaIndex = Integer.parseInt(schema); // In the case of XML component
			schema = (String) MetaModel.get("SelectSchema")
				.getMetaProperty("schema").getValidValue(schemaIndex);					
		}
		catch (NumberFormatException ex) {
			// In the case of annotated POJO 
		}
		setNewDefaultSchema(schema);
		super.execute();
		
		// restart all modules less SelectSchema
		Collection managers = (Collection) getContext().getAll("manager");
		Iterator it = managers.iterator();
		while(it.hasNext()){
			ModuleManager manager = (ModuleManager) it.next();
			if (!manager.getModuleName().equalsIgnoreCase("SelectSchema")) manager.reset();
		}
	}

}
