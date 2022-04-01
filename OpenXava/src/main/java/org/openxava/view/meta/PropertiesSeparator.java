package org.openxava.view.meta;



import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * It's used in a property collection for
 * indicate a separation. <p>
 *
 * Extends from <tt>MetaProperty</tt> thus it can be processed
 * by any method that receive a <tt>MetaProperty</tt>.<br> 
 * 
 * @author Javier Paniza
 */

public class PropertiesSeparator extends MetaProperty {
	
	public static final PropertiesSeparator INSTANCE = new PropertiesSeparator(); 
	
	
	
	private PropertiesSeparator() {
	}
	
	public String getName() {
		return "[SEPARATOR]";
	}
	
	public String getLabel() {
		return "";
	}
	
}
