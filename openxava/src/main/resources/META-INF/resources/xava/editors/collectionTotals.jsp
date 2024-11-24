<%@ include file="../imports.jsp"%>

<%@page import="org.openxava.web.WebEditors"%>
<%@page import="org.openxava.model.meta.MetaProperty"%>
<%@page import="org.openxava.view.View"%>
<%@page import="org.openxava.util.XavaPreferences"%> 
<%@page import="java.util.List"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>


<%
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
String collectionName = request.getParameter("collectionName");
View subview = view.getSubview(collectionName);
String idCollection = org.openxava.web.Collections.id(request, collectionName);
String propertyPrefix = request.getParameter("propertyPrefix");
String collectionPrefix = propertyPrefix == null?collectionName + ".":propertyPrefix + collectionName + ".";
String collectionArgv=",collection="+collectionName; 

boolean elementCollection = subview.isRepresentsElementCollection(); 
int additionalTotalsCount = subview.getCollectionTotalsCount();

List<MetaProperty> keyPropertiesList = subview.addKeyPropertiesOfReferencesEntity();
int mpListSize = 0;
if (!keyPropertiesList.isEmpty()) {
	mpListSize = subview.getMetaPropertiesList().size() - keyPropertiesList.size();
}

for (int i=0; i<additionalTotalsCount; i++) {
%>
	<tr class="<%=style.getTotalRow()%>">
	<% if (!(subview.isRepresentsElementCollection() && !subview.isCollectionEditable())) { %>
		<td/>
		<% if (!subview.getMetaCollection().isElementCollection()) { %>
		<td/>
		<% } %>
	<% } %>	
<%
java.util.Iterator it = subview.getMetaPropertiesList().iterator(); 
for (int c = 0; it.hasNext(); c++) {
	MetaProperty p = (MetaProperty) it.next();
	String align =p.isNumber() && !p.hasValidValues()?"ox-text-align-right":"";
	if (subview.hasCollectionTotal(i, c)) {
	%> 	
	<td class="ox-total-cell <%=align%>">	
	<div id="<xava:id name='<%="collection_total_" + i + "_" + c + "_" + collectionPrefix%>'/>" class=" <xava:id name='<%=idCollection%>'/>_col<%=c%>">
	<jsp:include page="collectionTotal.jsp">
		<jsp:param name="row" value="<%=i%>"/>
		<jsp:param name="column" value="<%=c%>"/>
	</jsp:include>
	</div>	
	</td>
	<%
	}
	else if (i==0 && XavaPreferences.getInstance().isSummationInList() && subview.isCollectionTotalCapable(c)) { 
	%>
	<td class="ox-total-capable-cell" <%=(mpListSize>0 && c>=mpListSize) ? "hidden" : ""%>>
		<div class=" <xava:id name='<%=idCollection%>'/>_col<%=c%>"> 
			<xava:action action='CollectionTotals.sumColumn' argv='<%="property="+p.getName() + collectionArgv%>'/>&nbsp;
		</div>	
	</td>
	<%
	}
	else if (subview.hasCollectionTotal(i, c + 1) && (i > 0 || !subview.hasCollectionSum(c + 1))) { 	
	%>
	<td class="ox-total-label-cell">
		<%=subview.getCollectionTotalLabel(i, c + 1)%>&nbsp;
	</td>
	<%	
	}
	else {
	%>	 
	<td/>
	<%		
	}	
	if (elementCollection && subview.isLastSearchKey(p.getName())) { 
	%>
	<td/>	
	<%	
	}
}
%>
</tr>
<%
} // for additionalTotalsCount 
%>
