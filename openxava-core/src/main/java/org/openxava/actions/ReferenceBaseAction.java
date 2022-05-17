package org.openxava.actions;

import javax.inject.*;

import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;

/**
 * @author Javier Paniza
 */

public class ReferenceBaseAction extends ViewBaseAction {
	
	static protected class ViewInfo {
		
		private View view;		
		private String memberName;
		private View parent;		
		
		ViewInfo(View view, String memberName, View parent) {
			this.view = view;
			this.memberName = memberName;
			this.parent = parent;
		}
		
		public View getView() {
			return view;
		}
		public String getMemberName() {
			return memberName;
		}
		public View getParent() {
			return parent;
		}
		
	}
	
	
	private ViewInfo viewInfo;	
	private String keyProperty;
	
	
	@Inject
	private View referenceSubview;
	
	public void execute() throws Exception {
		this.viewInfo = createSubview(getView(), createMemberName());		
		setReferenceSubview(viewInfo.getView());				
	}
	
	protected ViewInfo getViewInfo() {
		return viewInfo;
	}

	private ViewInfo createSubview(View view, String memberName) throws XavaException {
		if (memberName.endsWith(DescriptionsLists.COMPOSITE_KEY_SUFFIX)) memberName = memberName.substring(0, memberName.length() - 7);
		if (view.isRepresentsElementCollection()) {
			int idx = memberName.indexOf('.');
			int row = Integer.parseInt(memberName.substring(0, idx)); 
			memberName = memberName.substring(idx + 1);
			view.setCollectionEditingRow(row);
		}
		int idx = memberName.indexOf('.'); 
		if (idx < 0) {
			return new ViewInfo(view.getSubview(memberName), memberName, view); 
		}
		String subviewName = memberName.substring(0, idx);
		String nextMember = memberName.substring(idx + 1);		
		return createSubview(view.getSubview(subviewName), nextMember);
	}
	
	private String createMemberName() {
		String propertyName = getKeyProperty(); 
		int idx = propertyName.lastIndexOf(".");		
		if (idx >= 0) return propertyName.substring(0, idx);		
		return propertyName;
	}

	public String getKeyProperty() {
		return keyProperty;
	}

	public void setKeyProperty(String string) {
		keyProperty = string;		
	}

	public View getReferenceSubview() { 
		return referenceSubview;
	}

	public void setReferenceSubview(View view) { 
		referenceSubview = view;
	}

}
