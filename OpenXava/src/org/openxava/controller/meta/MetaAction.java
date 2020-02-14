package org.openxava.controller.meta;


import java.util.*;

import javax.servlet.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;

/**
 * 
 * @author Javier Paniza
 */

public class MetaAction extends MetaControllerElement implements Cloneable { 
	
	private static Log log = LogFactory.getLog(MetaAction.class);
	
	public static final int NEVER = -1;
	/** @since 4m6 */
	public static final int ALMOST_NEVER = 0; 
	public static final int IF_POSSIBLE = 1;
	public static final int ALMOST_ALWAYS = 2;
	public static final int ALWAYS = 4;
	
	private static Map tokensForKeystroke;
	private boolean hidden = false;
	private Collection metaSets;
	private String qualifiedName;
	private String method;
	private String keystroke;
	private String className;
	private Collection metaUseObjects;
	private MetaController metaController;
	private int byDefault;
	private boolean onInit;
	private boolean takesLong;
	private boolean confirm;
	private boolean onEachRequest;
	private boolean beforeEachRequest;
	private boolean afterEachRequest; 
	private boolean inEachRow; 
	private boolean processSelectedItems;
	private boolean availableOnNew; 
	private boolean losesChangedData; 
	
	public MetaAction() {
	}
	
	public MetaAction(String name) {
		setName(name);
	}
	
	public String getQualifiedName() {
		if (qualifiedName == null) {
			if (metaController == null) qualifiedName = getName();
			else qualifiedName = getMetaController().getName() + "." + getName();
		}	 
		return qualifiedName;
	}
	
	public void setName(String newName) {
		qualifiedName = null;
		super.setName(newName);
	}

	public String getKeystroke() {
		return keystroke;
	}
	public void setKeystroke(String keystroke) {
		if (!Is.emptyString(keystroke)) {
			this.keystroke = Strings.change(keystroke.toUpperCase(), getTokensForKeystroke());
		}		
		else {
			this.keystroke = keystroke;
		}
	}
	
	public MetaAction cloneMetaAction() { 
		try {
			return (MetaAction) clone();
		}
		catch (CloneNotSupportedException ex) {
			log.error(ex.getMessage(), ex);
			throw new RuntimeException(XavaResources.getString("implement_cloneable_required")); 
		}

	}
	
	public boolean hasKeystroke() {
		return !Is.emptyString(keystroke);
	}

	public String getMethod() {
		if (Is.emptyString(method)) return getName();
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getLabel(Locale locale) {
		return Labels.removeUnderlined(super.getLabel(locale));		
	}

	public char getMnemonic() {
		String label = super.getLabel();
		int idxSub = label.indexOf('_');
		if (idxSub >= 0) {
			int idxMnemonic = idxSub + 1;
			if (idxMnemonic < label.length()) {
				return label.charAt(idxMnemonic);
			}			
		}		
		return 0;
	}
	
	public boolean equals(Object action) {
		if (!(action instanceof MetaAction)) return false; // It also discards the nulls
		return getQualifiedName().equals(((MetaAction) action).getQualifiedName()); 
	}
	
	public int hashCode() {	
		return getName().hashCode();
	}
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String string) {
		className = string;
	}
	
	public boolean usesObjects() {
		return metaUseObjects != null && !metaUseObjects.isEmpty();
	}
	
	public Collection getMetaUseObjects() {		
		if (!usesObjects()) return Collections.EMPTY_LIST;
		return metaUseObjects;
	}

	public void addMetaUseObject(MetaUseObject object) {
		if (metaUseObjects == null) metaUseObjects = new ArrayList();
		metaUseObjects.add(object);		
	}

	public MetaController getMetaController() {
		return metaController;
	}
	public void setMetaController(MetaController controller) {
		metaController = controller;
		qualifiedName = null;
	}
	
	public String getControllerName() {
		return metaController==null?"":metaController.getName();
	}
	
	public void _addMetaSet(MetaSet metaSet) {
		if (metaSets == null) {
			metaSets = new ArrayList();
		}
		metaSets.add(metaSet);		
	}
	
	public IAction createAction() throws XavaException {
		try {
			Object o = Class.forName(getClassName()).newInstance();
			if (!(o instanceof IAction)) {
				throw new XavaException("implements_required", getClassName(), IAction.class.getName());
			}
			IAction calculator = (IAction) o;
			if (hasMetaSets()) {
				assignPropertyValues(calculator);
			}						
			return calculator;
		}
		catch (XavaException ex) {
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(),ex);
			throw new XavaException("create_action_error", getClassName());
		}
	}
	
	public boolean hasMetaSets() {
		return metaSets != null;
	}

	private void assignPropertyValues(IAction action) throws Exception {
		PropertiesManager mp = new PropertiesManager(action);
		Iterator it = getMetaSets().iterator();
		while (it.hasNext()) {
			MetaSet metaSet = (MetaSet) it.next();
			mp.executeSetFromString(metaSet.getPropertyName(), metaSet.getValue());			
		}		
	}

	public Collection getMetaSets() {
		return metaSets==null?new ArrayList():metaSets;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean b) {
		hidden = b;
	}

	public int getByDefault() {
		return byDefault;
	}
	public void setByDefault(int i) {
		byDefault = i;
	}
	
	public String getId() {
		return getQualifiedName();
	}

	public boolean isOnInit() {
		return onInit;
	}
	public void setOnInit(boolean b) {
		onInit = b;
	}

	public boolean isTakesLong() {
		return takesLong;
	}
	public void setTakesLong(boolean takesLong) {
		this.takesLong = takesLong;
	}

	public boolean isConfirm() {
		return confirm;
	}
	
	public String getConfirmMessage() {
		return getConfirmMessage(Locale.getDefault());
	}
	
	
	/** @param argv  Since 4m5 */
	public String getConfirmMessage(Locale locale, String... argv) {
		if (!isConfirm()) return "";
		String description = getDescription(locale);
		if (Is.emptyString(description)) description = getLabel(locale); 
		if (argv != null && argv.length > 0) return XavaResources.getString(locale, "are_you_sure_row_action", description, argv[0]);
		if (Is.emptyString(description)) return XavaResources.getString(locale, "are_you_sure"); 
		return XavaResources.getString(locale, "are_you_sure_action", description);
	}	
	
	/** @since 4m5*/
	public String getConfirmMessage(ServletRequest request, String argv){
		int rowIdx = argv.indexOf("row=");
		if (rowIdx < 0) return getConfirmMessage(request); 
		String row = argv.substring(rowIdx + 4);
		if (row.indexOf(",") >= 0 ) row = row.substring(0, row.indexOf(","));
		// because row index start in 0
		try{
			int a = Integer.valueOf(row);
			a++;
			row = String.valueOf(a);	
		}
		catch(NumberFormatException ex){
		}
		// 
		return getConfirmMessage(getLocale(request), row);
	}
	
	public String getConfirmMessage(ServletRequest request) {
		return getConfirmMessage(getLocale(request));
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}
	
	private static Map getTokensForKeystroke() { 		
		if (tokensForKeystroke == null) {
			tokensForKeystroke = new HashMap();
			tokensForKeystroke.put("CONTROL", "control");
			tokensForKeystroke.put("ALT", "alt");
			tokensForKeystroke.put("SHIFT", "shift");
			tokensForKeystroke.put("CTRL", "control");
			tokensForKeystroke.put("CNTRL", "control");
		}
		return tokensForKeystroke;
	}
	
	public boolean isOnEachRequest() {
		return onEachRequest;
	}
	public void setOnEachRequest(boolean b) {
		onEachRequest = b;
	}

	public boolean isBeforeEachRequest() {
		return beforeEachRequest;
	}

	public void setBeforeEachRequest(boolean beforeEachRequest) {
		this.beforeEachRequest = beforeEachRequest;
	}

	public void setInEachRow(boolean inEachRow) {
		this.inEachRow = inEachRow;
	}

	public boolean isInEachRow() {
		return inEachRow;
	}

	public String toString() {
		return getQualifiedName();
	}
	
	public boolean isAfterEachRequest() {
		return afterEachRequest;
	}

	public void setAfterEachRequest(boolean afterEachRequest) {
		this.afterEachRequest = afterEachRequest;
	}

	public boolean isProcessSelectedItems() {
		return processSelectedItems;
	}

	public void setProcessSelectedItems(boolean processSelectedItems) {
		this.processSelectedItems = processSelectedItems;
	}

	/**
	 * @since 5.8
	 */
	public boolean inNewWindow() { 
		try {
			Object action = Class.forName(getClassName()).newInstance();
			if (action instanceof IForwardAction) {
				return ((IForwardAction) action).inNewWindow();
			}
			return false;
		}
		catch (Exception ex) {
			return false;
		}
	}

	public boolean isAvailableOnNew() {
		return availableOnNew;
	}

	public void setAvailableOnNew(boolean availableOnNew) {
		this.availableOnNew = availableOnNew;
	}
	
	/** @since 6.3 */
	public boolean isLosesChangedData() { 
		return losesChangedData;
	}

	/** @since 6.3 */	
	public void setLosesChangedData(boolean losesChangedData) {
		this.losesChangedData = losesChangedData;
	}
	
}