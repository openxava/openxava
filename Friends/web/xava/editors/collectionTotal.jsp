<%@ include file="../imports.jsp"%> 

<%@page import="org.openxava.web.WebEditors"%>
<%@page import="org.openxava.model.meta.MetaProperty"%>
<%@page import="org.openxava.view.View"%>
<%@page import="org.openxava.util.XavaPreferences"%>
<%@page import="org.openxava.web.Collections"%>  

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>

<%
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
String collectionName = request.getParameter("collectionName");
String collectionArgv=",collection="+collectionName; 
View subview = view.getSubview(collectionName);
int row = Integer.parseInt(request.getParameter("row"));
int column = Integer.parseInt(request.getParameter("column"));
%>

<%
if (subview.isCollectionTotalEditable(row, column)) { 
%>
	<xava:editor property="<%=subview.getCollectionTotalPropertyName(row, column)%>"/>  				
<% 
} 
else { 
	MetaProperty p = (MetaProperty) subview.getMetaPropertiesList().get(column);
	Object total = subview.getCollectionTotal(row, column);
	String ftotal = WebEditors.format(request, p, total, errors, view.getViewName(), true);
	View rootView = view.getCollectionRootOrRoot();
	String sumProperty = collectionName + "." + p.getName() + "_SUM_";
	if (rootView.isPropertyUsedInCalculation(sumProperty)) {
		String script = Collections.sumPropertyScript(request, rootView, sumProperty); 
%>
		<input id="<xava:id name='<%=sumProperty%>'/>" type="hidden" value="<%=ftotal%>" <%=script%>/>
<% 		
	}
	else if (!subview.isCollectionFixedTotal(column) && XavaPreferences.getInstance().isSummationInList()) {
%>
		<xava:action action='CollectionTotals.removeColumnSum' argv='<%="property="+p.getName() + collectionArgv%>'/>
<% 
	} 
%>
	<nobr>
	<%=ftotal%>&nbsp;
	</nobr>
<%
}
%>
