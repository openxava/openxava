package org.openxava.actions;

import org.openxava.util.*;
import org.openxava.web.meta.*;

/**
 * 
 * @author Javier Paniza
 */
public class SelectListFormatAction extends TabBaseAction { 
	
	private String editor;

	public void execute() throws Exception {
		if (!Is.emptyString(getTab().getEditor())) {
			String releaseAction = MetaWebEditors.getMetaEditorByName(getTab().getEditor()).getReleaseAction();
			if (!Is.emptyString(releaseAction)) {
				executeAction(releaseAction); 
			}
		}
		String editorToInit = editor==null?getTab().getEditor():editor;
		if (!"__NONAME__".equals(editorToInit) && !Is.emptyString(editorToInit)) {
			String initAction = MetaWebEditors.getMetaEditorByName(editorToInit).getInitAction();
			if (!Is.emptyString(initAction)) {
				executeAction(initAction);
			}	
		}
		
		if (!Is.equal(getTab().getEditor(), editor)) { 
			if (editor != null) getTab().setEditor(editor);		
			if ("__NONAME__".equals(editor)) getTab().setEditor("");
			getManager().setActionsChanged(true);
		}
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}
		
}
