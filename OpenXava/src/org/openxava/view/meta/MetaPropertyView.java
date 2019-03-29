package org.openxava.view.meta;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 * @author Trifon Trifonov
 */
public class MetaPropertyView extends MetaMemberView implements java.io.Serializable {
	
	public final static int NORMAL_LABEL = 0;
	public final static int SMALL_LABEL = 1;
	public final static int NO_LABEL = 2;
	
	private static Log log = LogFactory.getLog(MetaPropertyView.class);
		
	private String propertyName;
	private String label;
	private boolean readOnly;
	private String onChangeActionClassName;	
	private int labelFormat = XavaPreferences.getInstance().getDefaultLabelFormat();
	private int displaySize;
	private String labelStyle;
	
	
	public String getPropertyName() {
		return propertyName==null?"":propertyName.trim();
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean b) {
		readOnly = b;
	}
	
	public boolean hasOnChangeAction() {
		return !Is.emptyString(getOnChangeActionClassName());
	}
	
	public IOnChangePropertyAction createOnChangeAction() throws XavaException {
		try {
			Object o = Class.forName(getOnChangeActionClassName()).newInstance();
			if (!(o instanceof IOnChangePropertyAction)) {
				throw new XavaException("on_change_action_implements_error", IOnChangePropertyAction.class.getName(), getOnChangeActionClassName());
			}
			IOnChangePropertyAction action = (IOnChangePropertyAction) o;
			return action;
		}
		catch (XavaException ex) {
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_error", getOnChangeActionClassName());
		}		
	}	

	public String getOnChangeActionClassName() {
		return onChangeActionClassName;
	}
	public void setOnChangeActionClassName(String string) {
		onChangeActionClassName = string;
	}

	public int getLabelFormat() {
		return labelFormat;
	}
	public void setLabelFormat(int labelFormat) {
		this.labelFormat = labelFormat;
	}

	// @Trifon
	public int getDisplaySize() {
		return displaySize;
	}
	// @Trifon
	public void setDisplaySize(int displaySize) {
		this.displaySize = displaySize;
	}

	public String getLabelStyle() {
		return labelStyle;
	}

	public void setLabelStyle(String labelStyle) {
		this.labelStyle = labelStyle;
	}
	
	public void addLabelStyle(String labelStyle){
		if (this.labelStyle == null) this.labelStyle = "";
		this.labelStyle = this.labelStyle + " " + labelStyle;
	}
	
}
