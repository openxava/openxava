package org.openxava.filters;

import java.util.*;

import org.openxava.util.*;

/**
 * Used for implementing collections with condition using a 
 * {@link org.openxava.tab.Tab} from a {@link org.openxava.view.View}. <p>
 * 
 * @author Javier Paniza
 */

public class CollectionWithConditionInViewFilter extends CollectionInViewBaseFilter {
		
	private Collection conditionArgumentsPropertyNames;

	protected Collection getKeyValues() throws XavaException { 		
		Collection keyNames = getConditionArgumentsPropertyNames();  
		Collection values = new ArrayList();			
		for (Iterator it = keyNames.iterator(); it.hasNext();) {
			String keyName = (String) it.next();
			values.add(getView().getValue(keyName));				
		}			
		return values;		
	}

	public Collection getConditionArgumentsPropertyNames() {
		return conditionArgumentsPropertyNames;
	}

	public void setConditionArgumentsPropertyNames(Collection conditionArgumentsPropertyNames) {
		this.conditionArgumentsPropertyNames = conditionArgumentsPropertyNames;
	}
	
}
