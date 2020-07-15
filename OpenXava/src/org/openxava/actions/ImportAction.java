package org.openxava.actions;

import java.util.*;

import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.model.transients.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;

/**
 * 
 * @author Javier Paniza
 */

public class ImportAction extends ViewBaseAction implements IChangeControllersAction { 
	
	private String[] nextControllers = DEFAULT_CONTROLLERS;
	
	public void execute() throws Exception {
		getView().updateModelFromView();
		Import imp = (Import) getView().getModel();
		if (!validateRequired(imp)) return;
		Scanner scanner = new Scanner(imp.getData());
		if (scanner.hasNextLine()) scanner.nextLine(); // We skip first line, the header
		String separator = XavaPreferences.getInstance().getCSVSeparator();
		int count = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (Is.emptyString(line)) continue;
			Map values = new HashMap();
			String[] fields = line.split(separator);
			int x = 0;
			for (ImportColumn column: imp.getColumns()) {
				if (x >= fields.length) break;
				String rawValue = fields[x];
				if (!Is.empty(column.getNameInApp())) {
					View moduleView = getPreviousView();
					MetaProperty p = moduleView.getMetaModel().getMetaProperty(column.getNameInApp());
					String filteredValue = Strings.unquote(Import.decodeSeparators(rawValue)); 
					Object value = WebEditors.parse(getRequest(), p, filteredValue, getErrors(), moduleView.getViewName()); 
					values.put(column.getNameInApp(), value);
				}
				x++;
			}
			try {
				values = Maps.plainToTree(values); 
				MapFacade.create(imp.getModelName(), values);
				count++;
			}
			catch (org.openxava.validators.ValidationException ex) {
				Messages errors = ex.getErrors();
				for (Object message: errors.getStrings(getRequest())) {
					addError("import_error", values, message); 
				}
			}
			catch (Exception ex) {
				addError("import_error", values, ex.getMessage());
			}
		}
		if (count > 0) addMessage("records_imported", count);
		else addError("no_records_imported");
		closeDialog();
	}

	private boolean validateRequired(Import imp) {
		MetaModel metaModel = MetaModel.get(imp.getModelName());
		Collection<String> requiredMembers = new ArrayList(metaModel.getRequiredMemberNames());
		for (ImportColumn column: imp.getColumns()) {
			requiredMembers.remove(Strings.firstToken(column.getNameInApp(), ".")); 
		}		
		if (requiredMembers.isEmpty()) return true;
		StringBuffer members = new StringBuffer(); 
		for (String member: requiredMembers) {
			if (members.length() > 0) members.append(", ");
			members.append(metaModel.getMetaMember(member).getLabel());
		}
		addError("import_required_members", members.toString());
		nextControllers = SAME_CONTROLLERS;
		return false;
	}

	public String[] getNextControllers() throws Exception {
		return nextControllers;
	}
	
}
