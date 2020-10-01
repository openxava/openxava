package org.openxava.actions;

import java.util.*;
import java.util.stream.*;

import javax.ejb.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */

public class SaveAction extends TabBaseAction {
		
	private boolean resetAfterOnCreate = true; 
	private boolean resetAfterOnModify = true; 
	private boolean resetAfter = true;
	private boolean refreshAfter = true; 
    
	public void execute() throws Exception {
		try {
			if (getView().isKeyEditable()) {
				Map values = create();
				updateView(values, isResetAfterOnCreate());
			}
			else {
				Map values = modify();
				updateView(values, isResetAfterOnModify());
			}
			
			resetDescriptionsCache();
		}
		catch (ValidationException ex) {			
			addErrors(ex.getErrors());
		}
		catch (ObjectNotFoundException ex) {			
			addError("no_modify_no_exists");
		}
		catch (DuplicateKeyException ex) {
			addError("no_create_exists");
		}
	}

	protected void updateView(Map values, boolean resetAfter) { 
		if (resetAfter) {
			getView().setKeyEditable(true);
			commit(); // If we change this, we should run all test suite using READ COMMITED (with hsqldb 2 for example)
			getView().reset();				
		}
		else {				
			getView().setKeyEditable(false);				
			if (isRefreshAfter()) getView().setValues(values); 
		}
	}

	protected Map modify() throws Exception {
		Map values = null;
		Map keyValues = getView().getKeyValues();		
		MapFacade.setValues(getModelName(), keyValues, getValuesToSave());
		addMessage("entity_modified", getModelName());
		if (!isResetAfterOnModify() && isRefreshAfter()) {
			getView().clear(); 
			values = MapFacade.getValues(getModelName(), keyValues, getView().getMembersNamesWithHidden());
		}
		return values;
	}

	protected Map create() throws Exception {
		Map values = null;
		if (isResetAfterOnCreate() || (!isRefreshAfter() && !getView().getMetaModel().hasHiddenKey())) {
			Collection<String> possibleGeneratedProperties = getPossibleGeneratedProperties();
			if (possibleGeneratedProperties == null) {
				MapFacade.create(getModelName(), getValuesToSave());
				addMessage("entity_created", getModelName());
			}
			else {
				Map keyValues = MapFacade.createReturningKey(getModelName(), getValuesToSave());
				String idValues = readPropertiesAsString(keyValues, possibleGeneratedProperties); 
				addMessage("'" + XavaResources.getString("entity_created", getModelName()) + ": " + idValues + "'");
			}
		}
		else {								
			Map keyValues = MapFacade.createReturningKey(getModelName(), getValuesToSave());					
			addMessage("entity_created", getModelName());
			if (isRefreshAfter()) {  
				getView().clear(); 
				values = MapFacade.getValues(getModelName(), keyValues, getView().getMembersNamesWithHidden());
			}
			else {
				getView().addValues(keyValues);
			}
		}
		getTab().reset();
		return values;
	}
	
	private String readPropertiesAsString(Map keyValues, Collection<String> properties) throws Exception { 
		Map membersNames = properties.stream()
			.collect(HashMap::new, (m,v) -> m.put(v, null), HashMap::putAll);
		Map values = MapFacade.getValues(getModelName(), keyValues, membersNames);
		return properties.stream()
			.map(p -> values.get(p))
			.filter(Objects::nonNull) 
			.map(Object::toString)
			.collect(Collectors.joining("/"));
	}

	private Collection<String> getPossibleGeneratedProperties() throws Exception { 
		MetaModel metaModel = getView().getMetaModel();
		if (!metaModel.hasHiddenKey()) return null;
		
		Collection<String> result = metaModel.getMembersNames().stream()
			.filter(m -> metaModel.containsMetaProperty(m))
			.map(m -> metaModel.getMetaProperty(m))
			.filter(MetaProperty::isSearchKey)
			.map(MetaProperty::getName)
			.collect(Collectors.toList());
		
		if (result.isEmpty()) {
			List<String> properties = metaModel.getPropertiesNamesWithoutHiddenNorTransient();
			if (properties.size() > 0) result.add(properties.get(0));
			if (properties.size() > 1) result.add(properties.get(1));
		}
				
		boolean hasEmpty = result.stream().map(p -> getView().getValue(p)).anyMatch(v -> Is.empty(v));
		if (!hasEmpty) return null;
		
		return result;
	}
	
	protected Map getValuesToSave() throws Exception {		
		return getView().getValues();
	}
	
	/**
	 * If <tt>true</tt> reset the form after save, else refresh the
	 * form from database displayed the recently saved data. <p>
	 * 
	 * The default value is <tt>true</tt>.
	 */
	public boolean isResetAfter() {
		return resetAfter;
	}

	/**
	 * If <tt>true</tt> reset the form after save, else refresh the
	 * form from database displayed the recently saved data. <p>
	 * 
	 * The default value is <tt>true</tt>.
	 */
	public void setResetAfter(boolean b) {
		resetAfter = b;
		resetAfterOnCreate = b; 
		resetAfterOnModify = b; 
	}

	/**
	 * If <tt>false</tt> after save does not refresh the
	 * form from database. <p>
	 * 
	 * It only has effect if <tt>resetAfter</tt> is <tt>false</tt>.
	 * 
	 * The default value is <tt>true</tt>.
	 * 
	 * @since 4.8
	 */	
	public boolean isRefreshAfter() {
		return refreshAfter;
	}

	/**
	 * If <tt>false</tt> after save does not refresh the
	 * form from database. <p>
	 * 
	 * It only has effect if <tt>resetAfter</tt> is <tt>false</tt>.
	 * 
	 * The default value is <tt>true</tt>.
	 * 
	 * @since 4.8
	 */		
	public void setRefreshAfter(boolean refreshAfter) {
		this.refreshAfter = refreshAfter;
	}

	/**
	 * If <tt>true</tt> reset the form after save <b>for creating</b>, else refresh the
	 * form from database displayed the recently saved data. <p>
	 * 
	 * The default value is <tt>true</tt>.
	 * 
	 * @since 5.8
	 */	
	public boolean isResetAfterOnCreate() {
		return resetAfterOnCreate;
	}

	/**
	 * If <tt>true</tt> reset the form after save <b>for creating</b>, else refresh the
	 * form from database displayed the recently saved data. <p>
	 * 
	 * The default value is <tt>true</tt>.
	 * 
	 * @since 5.8
	 */	
	public void setResetAfterOnCreate(boolean resetAfterOnCreate) {
		this.resetAfterOnCreate = resetAfterOnCreate;
	}

	/**
	 * If <tt>true</tt> reset the form after save <b>for modifying</b>, else refresh the
	 * form from database displayed the recently saved data. <p>
	 * 
	 * The default value is <tt>true</tt>.
	 * 
	 * @since 5.8
	 */
	public boolean isResetAfterOnModify() {
		return resetAfterOnModify;
	}

	/**
	 * If <tt>true</tt> reset the form after save <b>for modifying</b>, else refresh the
	 * form from database displayed the recently saved data. <p>
	 * 
	 * The default value is <tt>true</tt>.
	 * 
	 * @since 5.8
	 */	
	public void setResetAfterOnModify(boolean resetAfterOnModify) {
		this.resetAfterOnModify = resetAfterOnModify;
	}

}
