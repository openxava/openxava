<%@ include file="../imports.jsp"%>

<%@page import="org.openxava.view.View"%>
<%@page import="org.openxava.model.MapFacade"%>
<%@page import="org.openxava.test.model.Blog"%>
<%@page import="org.openxava.test.model.BlogComment"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.text.DateFormat"%>
<%@page import="org.openxava.util.Locales"%>
<%@page import="org.openxava.util.Is"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<% 
String viewObject = request.getParameter("viewObject");
View collectionView = (View) context.get(request, viewObject);
View rootView = collectionView.getRoot();
Map key = rootView.getKeyValues();
if (Is.empty(key)) {
%>	
There are no comments	
<%
} else {
	
Blog blog = (Blog) MapFacade.findEntity("Blog", key);
String action = request.getParameter("rowAction"); 
String actionArgv = ",viewObject=" + viewObject;  
%>

These are the comments:<br/>
<%
DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locales.getCurrent());
int f=0;
for (Iterator it = blog.getComments().iterator(); it.hasNext(); f++) {
	BlogComment comment = (BlogComment) it.next();	
%>
<i><b><big>Comment at <%=df.format(comment.getDate())%></big></b></i>
<xava:action action='<%=action%>' argv='<%="row=" + f + actionArgv%>'/>
<p>
<i><%=comment.getBody()%></i>
</p>
<hr/>
<%
}

}
%>


