package org.openxava.actions;

import java.text.*;
import java.util.*;

import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * @author Chungyen Tsai
 */
public class CalendarViewDetailAction extends ViewDetailAction{
	
	private String calendarKey;
	
	public void execute() throws XavaException, ParseException {
		getView().setModelName(model);
		getView().setViewName(getManager().getXavaViewName());
		setAtListBegin(false);
		setNoElementsInList(false);
		
		key = new HashMap<>();
		MetaModel metaModel = getView().getMetaView().getMetaModel();
		String[] calendarKeys = calendarKey.split("_");
		List<String> allKeyPropertiesNames = new ArrayList<>(metaModel.getAllKeyPropertiesNames());
		List<MetaProperty> metaPropertyKeys = metaModel.getAllMetaPropertiesKey();
		for (int i = 0; i < calendarKeys.length; i++) {
			MetaProperty property = metaPropertyKeys.get(i);
			Object keyObject = property.parse(calendarKeys[i]);
			key.put(allKeyPropertiesNames.get(i).toString(), keyObject);
		}
		getView().setValues(key);
	}
	
	public String getCalendarKey() {
		return calendarKey;
	}

	public void setCalendarKey(String calendarKey) {
		this.calendarKey = calendarKey;
	}
}
