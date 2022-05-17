<%@ include file="../imports.jsp"%>

<%@page import="java.text.DateFormat"%>
<%@page import="org.openxava.util.Users"%>
<%@page import="org.openxava.util.Locales"%>
<%@page import="org.openxava.util.XavaResources"%>
<%@page import="org.openxava.web.editors.DiscussionComment"%>
<%@page import="java.util.UUID"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Collection"%>
<%@page import="org.openxava.model.meta.MetaProperty" %>
<%@page import="org.openxava.web.Ids"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<% 
String contextPath = (String) request.getAttribute("xava.contextPath");
if (contextPath == null) contextPath = request.getContextPath();
String version = org.openxava.controller.ModuleManager.getVersion();
%>	
<script type='text/javascript' src='<%=contextPath%>/dwr/interface/Discussion.js?ox=<%=version%>'></script>	

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String discussionId = (String) request.getAttribute(propertyKey + ".value");
if (discussionId == null) discussionId = UUID.randomUUID().toString().replace("-", ""); 
boolean editable="true".equals(request.getParameter("editable"));
%>
<div class="ox-discussion" id="xava_comments_<%=discussionId%>">   
<%
Collection<DiscussionComment> comments = new ArrayList<DiscussionComment>(DiscussionComment.findByDiscussion(discussionId));
DiscussionComment templateComment = new DiscussionComment();
templateComment.setUserName(Users.getCurrent());
comments.add(templateComment);

for (DiscussionComment comment: comments) {
		String hidden = comment.getId() == null?"style='display:none;'":"";
		String formattedTime = comment.getTime()==null?XavaResources.getString(request, "now"):DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locales.getCurrent()).format(comment.getTime());
%>
	<div class="ox-discussion-comment" <%=hidden%>> 
		<div class="ox-discussion-comment-header"><span class="ox-discussion-comment-author"><%=comment.getUserName()%></span> - <%=formattedTime%></div>
		<div class="ox-discussion-comment-content"><%=comment.getComment()%></div>  
	</div>
<%
}
%>
</div>

<% if (editable) { %>
<textarea id="xava_new_comment_<%=discussionId%>" class="ox-simple-ckeditor xava-new-comment" tabindex="1"></textarea>

<div id="xava_new_comment_<%=discussionId%>_buttons" class="ox-discussion-post-button">
	<input type="button" tabindex="1" class="<%=style.getButton()%>" style="display: none;" 
		onclick="discussionEditor.postMessage('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '<%=discussionId%>')" value="<xava:label key="addComment"/>"/>
	<input type="button" tabindex="1" class="<%=style.getButton()%>" style="display: none;" 
		onclick="discussionEditor.cancel('<%=discussionId%>')" 
		value="<xava:label key="cancel"/>"/>
</div>
<% } %>	

<input id="<%=propertyKey%>" type="hidden" name="<%=propertyKey%>" value="<%=discussionId%>">