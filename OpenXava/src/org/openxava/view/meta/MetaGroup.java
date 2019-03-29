package org.openxava.view.meta;

import org.apache.commons.lang3.*;
import org.apache.commons.logging.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class MetaGroup extends MetaMember {
	
	private static Log log = LogFactory.getLog(MetaGroup.class);
	
	private String membersNames;
	private boolean alignedByColumns = false;
	private MetaView metaView;
	private MetaView metaViewParent;
			
	public MetaGroup(MetaView parent) {
		this.metaViewParent = parent;
	}
	
	public MetaView getMetaView() throws XavaException {
		if (metaView == null) {
			try {
				metaView = (MetaView) metaViewParent.clone();
				metaView.setAlignedByColumns(isAlignedByColumns()); 
				metaView.setMembersNames(membersNames);		
			} 
			catch (CloneNotSupportedException e) {
				throw new XavaException("group_view_error_no_clone");			
			}			
		}
		return metaView;				
	}

	public void setMembersNames(String members) {
		this.membersNames = members;		
	}

	public boolean isAlignedByColumns() {
		return alignedByColumns;
	}

	public void setAlignedByColumns(boolean alignedByColumns) {
		this.alignedByColumns = alignedByColumns;
	}
	
	/**
	 * If {@code newName} is null or empty is replaced by {@code emptyGroup}. <p> 
	 * If {@code newName} contains blanks (\n, \r, \t, \f) or whitespace are 
	 * suppressed. <p> 
	 * 
	 * @since 5.2.1
	 */
	@Override
	public void setName(String newName) {
		if (Is.emptyString(newName)) {
			newName = "emptyGroup";
			log.warn(XavaResources.getString("group_name_not_allowed", "is empty", 
					   						 newName));			
		} else if (StringUtils.containsAny(newName, "\n\r\t\f ")) {
			log.warn(XavaResources.getString("group_name_not_allowed", newName, 
								   (newName = Strings.removeBlanks(newName))));					
		}
			
		super.setName(newName);
	}
}
