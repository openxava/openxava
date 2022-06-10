package org.openxava.web.dwr;

import java.util.*;

import javax.servlet.http.*;

import org.openxava.formatters.*;
import org.openxava.jpa.*;
import org.openxava.util.*;
import org.openxava.view.View;
import org.openxava.web.editors.*;

/**
 * 
 * @author Javier Paniza
 * @since 5.6
 */
public class Discussion extends DWRBase {
	
	public void postComment(HttpServletRequest request, HttpServletResponse response, String application, String module, String discussionId, String commentContent) {
		try {
			initRequest(request, response, application, module); 
			DiscussionComment comment = new DiscussionComment();
			comment.setDiscussionId(discussionId);
			comment.setUserName(Users.getCurrent());
			comment.setComment(commentContent);
			XPersistence.getManager().persist(comment);
			trackModification(request, application, module, discussionId, commentContent); 
			
		}
		finally {
			try {
				XPersistence.commit();
			}
			finally { 
				cleanRequest();
			}
		}
	}

	private void trackModification(HttpServletRequest request, String application, String module, String discussionId, String commentContent) { 
		View view = (View) getContext(request).get(application, module, "xava_view");
		String property = getDiscussionProperty(view.getValues(), discussionId);
		Map oldChangedValues = new HashMap();
		oldChangedValues.put(property, XavaResources.getString("discussion_new_comment")); 
		Map newChangedValues = new HashMap();
		String formattedContent = formatContent(request, commentContent);
		newChangedValues.put(property, formattedContent); 
		AccessTracker.modified(view.getModelName(), view.getKeyValues(), oldChangedValues, newChangedValues);
	}

	private String formatContent(HttpServletRequest request, String commentContent) { 
		try {
			return new HtmlTextListFormatter().format(request, commentContent);
		} 
		catch (Exception e) {
			return commentContent;
		}
	}

	private String getDiscussionProperty(Map values, String discussionId) {
		return (String) Maps.getKeyFromValue(values, discussionId, "DISCUSSION");
	}

}
