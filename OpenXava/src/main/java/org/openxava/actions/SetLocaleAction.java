package org.openxava.actions;

import java.util.Locale;

import org.openxava.util.Is;
import org.openxava.util.Locales;

/**
 * Changes locale based on url parameter.
 * 2011/mar/02 accepts language_COUNTRY for proper locale creation.
 * @author Federico Alcantara 
 */

public class SetLocaleAction extends BaseAction {
	public void execute() throws Exception {
		String language = getRequest().getParameter("locale");
		String country = null;
		if (!Is.emptyString(language)) {
			if (language.contains("_")) {
				country = language.split("_")[1].trim();
				language = language.split("_")[0].trim();
			}
			Locale locale = Is.emptyString(country) ? new Locale(language) : new Locale(language, country);
			getRequest().getSession().setAttribute("xava.portal.locale",
					locale);
			Locales.setCurrent(getRequest());
		}
	}

}
