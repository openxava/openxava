package org.openxava.actions;

import java.util.*;

import javax.ejb.*;
import javax.inject.*;

import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * Search using as key the data displayed in the view. <p>
 * 
 * First try to use the key value, if they are filled, 
 * otherwise uses the values of any properties filled for searching
 * the data, and return the first matched object.<p>
 * 
 * You can refine the behaviour of this action extending it and overwrite
 * its protected methods.<p>
 * 
 * @author Javier Paniza
 * @author Ana Andrés
 */

public class SearchByViewKeyAction extends ViewBaseAction {
	
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(SearchByViewKeyAction.class);
	
	@Inject
	@Named("mainTab")
	Tab tab;

	public void execute() throws Exception {
		System.out.println("SearchByViewKeyAction");
		Map keys = null;  
		Map valuesForSearchByAnyProperty = null;
		try {
			keys = getKeyValuesFromView();
			Map values = null;			
			if (Maps.isEmpty(keys)) {
				try {					
					valuesForSearchByAnyProperty = getValuesForSearchByAnyProperty();
					getView().clear();
					values = MapFacade.getValuesByAnyProperty(getModelName(), valuesForSearchByAnyProperty, getMemberNames());
				}
				catch (ObjectNotFoundException ex) {
					// This is for the case of key with 0 as valid value
					getView().clear(); 
					values = MapFacade.getValues(getModelName(), keys, getMemberNames());					
				}
			}
			else {
				System.out.println("else");
//				System.out.println(keys);
//				System.out.println(isSimpleMap(keys));
//				System.out.println(tab.containsKeys(keys));
				
				///DriverWithTab issue
				
				
				System.out.println(tab.getModelName()); 
				System.out.println(getView().getModelName());
				if (isSimpleMap(keys)) {
//					System.out.println("base1");
					System.out.println(Arrays.toString(tab.getConditionValues()));
					System.out.println(tab.getTotalSize());
					System.out.println("tabName " + tab.getTabName());
					Tab tab2 = tab.clone();
					tab2.clearCondition();
					//tab2.reloadMetaModel();
					tab2.reset(); 
					System.out.println("tabName " + tab2.getTabName());
					System.out.println(Arrays.toString(tab2.getConditionValues()));
//					System.out.println(tab2.getTotalSize());
					if (!tab2.containsKeys(keys) && tab.getModelName().equals(getView().getModelName())) {
						System.out.println("notfound");
						getView().clear();
						throw new ObjectNotFoundException();
					}
				}
				getView().clear();
				values = MapFacade.getValues(getModelName(), keys, getMemberNames());
			}
			getView().setEditable(true);	
			getView().setKeyEditable(false);			
			setValuesToView(values); 		
		}
		catch (ObjectNotFoundException ex) {
			String searchPropertiesAndValues = getSearchPropertiesAndValues(valuesForSearchByAnyProperty==null?keys:valuesForSearchByAnyProperty);	
			addError("object_not_found", getModelName(), searchPropertiesAndValues);			
		}						
		catch (Exception ex) {
			log.error(ex.getMessage(),ex);
			addError("system_error");			
		}
	}
	
	/**
	 * Executed after searching is done, in order to assign the searched
	 * values to the view. <p>
	 * 
	 * @param values The values to assign to the view
	 * @throws Exception If some is wrong.
	 */
	protected void setValuesToView(Map values) throws Exception { 
		getView().setValues(values);
	}

	/** 
	 * Names of the members to be retrieve from object model (at the end from database). <p>
	 * 
	 * By default, they are the members shown by the view.  
	 */
	protected Map getMemberNames() throws Exception{
		return getView().getMembersNamesWithHidden();
	}
	
	/**
	 * Values obtained from the view, used to do a search by any filled value. <p>
	 * 
	 * By default assumed all data currently displayed to the user.<br>
	 */
	protected Map getValuesFromView() throws Exception {
		return getView().getValues();
	}
	
	private Map getValuesForSearchByAnyProperty() throws Exception {
		return filter(getValuesFromView());
	}

	private Map filter(Map values) { 				
		for (Iterator it = values.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry en = (Map.Entry) it.next();
			if (en.getValue() instanceof String && !Is.empty(en.getValue())) { 
				values.put(en.getKey(), en.getValue() + "%");
			}
			else if (en.getValue() instanceof Map){
				filter((Map) en.getValue());
			}
		}
		return values;
	}

	/**
	 * Key values obtained from the view, used to do a search by key. <p>
	 * 
	 * By default assumed key data in the view.<br> 
	 */
	protected Map getKeyValuesFromView() throws Exception {
		return getView().getKeyValues();
	}

	private String getSearchPropertiesAndValues(Map keys) throws Exception{ 
		StringBuffer sb = new StringBuffer("");
		Map mapToSearch = Maps.treeToPlain(keys);
		MetaEntity metaEntity = MetaComponent.get(getModelName()).getMetaEntity();
		String separator = "";		
		Iterator it = mapToSearch.entrySet().iterator();		
		while (it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			Object property = entry.getKey();
			if (!Is.empty(entry.getValue())) {
				MetaProperty mp = metaEntity.getMetaProperty(property.toString());
				String propertyName = mp.getQualifiedLabel(getRequest());
				String value = WebEditors.format(getRequest(), mp, entry.getValue(), getErrors(), getView().getViewName());
				separator = sb.length() == 0 ? "" : ", ";
				sb.append(separator + propertyName + ":" + value);
			}
		}		
		return "'" + sb.toString().trim() + "'";
	}
/*
	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}
	*/
	/*
    private boolean isNestedMap(Map<String, Object> map) {
        for (Object value : map.values()) {
            if (value instanceof Map) {
                return true;
            }
        }
        return false;
    }*/
    
    public static boolean isSimpleMap(Map<String, Object> map) {
        for (Object value : map.values()) {
            if (value instanceof Map) {
            	System.out.println("vlue is map");
                return false;
            } else if (value instanceof Object[]) {
            	System.out.println("map is arr");
                Object[] array = (Object[]) value;
                for (Object element : array) {
                    if (element instanceof Map) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
	
}
