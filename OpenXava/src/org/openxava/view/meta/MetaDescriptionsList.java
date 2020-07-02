package org.openxava.view.meta;

import java.util.*;

import org.openxava.filters.meta.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class MetaDescriptionsList implements java.io.Serializable {
	
	private String descriptionPropertyName;
	private String descriptionPropertiesNames;	
	private String depends;
	private String condition;
	private String order;
	private boolean orderByKey;
	private boolean showReferenceView; 
	private Collection dependsNames;
	private int labelFormat = XavaPreferences.getInstance().getDefaultLabelFormat();
	private String forTabs;
	private String notForTabs;
	private String labelStyle = "";
	private MetaFilter metaFilter; 
	
		
	public String getDescriptionPropertyName() {
		return descriptionPropertyName;
	}
	
	public void setDescriptionPropertyName(String string) {
		descriptionPropertyName = string;
	}

	public String getCondition() {
		return condition==null?"":condition;
	}

	public String getDepends() {
		return depends==null?"":depends;
	}

	public void setCondition(String string) {
		condition = string;
	}

	public void setDepends(String string) {
		depends = string;
	}

	public boolean isOrderByKey() {
		return orderByKey;
	}

	public void setOrderByKey(boolean b) {
		orderByKey = b;
	}

	public String getDescriptionPropertiesNames() {
		return descriptionPropertiesNames==null?"":descriptionPropertiesNames;
	}
	public void setDescriptionPropertiesNames(
			String nombresPropiedadesDescripcion) {		
		this.descriptionPropertiesNames = nombresPropiedadesDescripcion;
	}

	public boolean dependsOn(MetaProperty p) { 
		return getDependsNames().contains(p.getName());		
	}

	private Collection getDependsNames() {
		if (dependsNames == null) {
			if (Is.emptyString(getDepends())) return Collections.EMPTY_LIST;
			dependsNames = new ArrayList();
			StringTokenizer st = new StringTokenizer(getDepends(), ",;");
			while (st.hasMoreTokens()) {
				String name = st.nextToken().trim();
				dependsNames.add(name);
			}
		}
		return dependsNames;
	}

	public String getOrder() {
		return order==null?"":order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public int getLabelFormat() {
		return labelFormat;
	}

	public void setLabelFormat(int labelFormat) {
		this.labelFormat = labelFormat;
	}

	public String getForTabs() {
		return forTabs == null ? "" : forTabs;
	}

	public void setForTabs(String forTabs) {
		this.forTabs = forTabs;
	}

	public String getNotForTabs() {
		return notForTabs == null ? "" : notForTabs;
	}

	public void setNotForTabs(String notForTabs) {
		this.notForTabs = notForTabs;
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

	public boolean isShowReferenceView() {
		return showReferenceView;
	}

	public void setShowReferenceView(boolean showReferenceView) {
		this.showReferenceView = showReferenceView;
	}

	public MetaFilter getMetaFilter() {
		return metaFilter;
	}

	public void setMetaFilter(MetaFilter metaFilter) {
		this.metaFilter = metaFilter;
	}

}
