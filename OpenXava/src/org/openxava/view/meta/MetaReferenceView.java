package org.openxava.view.meta;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.util.*;




/**
 * @author Javier Paniza
 */
public class MetaReferenceView extends MetaMemberView implements java.io.Serializable {
	
	private static Log log = LogFactory.getLog(MetaReferenceView.class);
	
	private String referenceName;
	private String viewName;
	private MetaSearchAction metaSearchAction;
	private MetaDescriptionsList metaDescriptionsList;
	private boolean frame = true;
	private boolean create = true;
	private boolean modify = true; 
	private boolean search = true;
	private boolean readOnly = false;
	private boolean asAggregate = false;
	private String onChangeSearchActionClassName; 

	public IOnChangePropertyAction createOnChangeSearchAction() throws XavaException { 
		try {			
			Object o = Class.forName(getOnChangeSearchActionClassName()).newInstance();
			if (!(o instanceof IOnChangePropertyAction)) {
				throw new XavaException("on_change_action_implements_error", IOnChangePropertyAction.class.getName(), getOnChangeSearchActionClassName());
			}
			IOnChangePropertyAction action = (IOnChangePropertyAction) o;			
			return action;
		}
		catch (XavaException ex) {
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new XavaException("create_error", getOnChangeSearchActionClassName());
		}		
	}
		
	public String getReferenceName() {
		return referenceName==null?"":referenceName.trim();
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public MetaSearchAction getMetaSearchAction() {
		return metaSearchAction;
	}

	public void setMetaSearchAction(MetaSearchAction metaSearchAction) {
		this.metaSearchAction = metaSearchAction;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public boolean isFrame() {
		return frame;
	}

	public void setFrame(boolean frame) {
		this.frame = frame;
	}
	
	public MetaDescriptionsList getMetaDescriptionsList() {
		return metaDescriptionsList;
	}

	public void setMetaDescriptionsList(MetaDescriptionsList descriptions) {
		metaDescriptionsList = descriptions;
	}

	public boolean isCreate() {
		return create;
	}
	public void setCreate(boolean create) {
		this.create = create;
	}
	public boolean isSearch() {
		return search;
	}
	public void setSearch(boolean search) {
		this.search = search;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isAsAggregate() {
		return asAggregate;
	}

	public void setAsAggregate(boolean asAggregate) {
		this.asAggregate = asAggregate;
	}

	public boolean isModify() {
		return modify;		
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}
	
	public String getOnChangeSearchActionClassName() {
		return onChangeSearchActionClassName;
	}

	public void setOnChangeSearchActionClassName(String searchOnChangeActionClassName) {
		this.onChangeSearchActionClassName = searchOnChangeActionClassName;
	}
	
}
