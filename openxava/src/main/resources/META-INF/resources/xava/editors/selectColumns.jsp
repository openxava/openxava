<%@ include file="../imports.jsp"%>

<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Locale" %> <%-- Trifon --%>
<%@ page import="org.openxava.util.Locales" %> <%-- Trifon --%>
<%@ page import="org.openxava.util.Labels" %> <%-- Trifon --%>
<%@ page import="org.openxava.util.Is" %> 
<%@ page import="org.openxava.web.Ids"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String tabObject = request.getParameter("tabObject");
tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
org.openxava.tab.Tab tab = (org.openxava.tab.Tab) context.get(request, "xava_customizingTab");
String searchWord = request.getParameter("searchWord");
searchWord = searchWord == null?"":searchWord.toLowerCase();
boolean originalColumnsToAddUntilSecondLevel = tab.isColumnsToAddUntilSecondLevel();
if (!Is.emptyString(searchWord)) tab.setColumnsToAddUntilSecondLevel(false);
%>
<table id="<xava:id name='xavaPropertiesList'/>" class='ox-list ox-select-columns-list' width="100%" <%=style.getListCellSpacing()%>>
<tr class="ox-list-pair"/> 
<%
int f=0;
Locale currentLocale = Locales.getCurrent(); //Trifon
int c=0; 
for (Iterator it=tab.getColumnsToAdd().iterator(); it.hasNext();) { 
	String property = (String) it.next();
	String cssClass=c%2==0?"ox-list-pair":"ox-list-odd";	
	String cssCellClass=c%2==0?"ox-list-pair":"ox-list-odd";
	String events=c%2==0?style.getListPairEvents():style.getListOddEvents();	
	String rowId = Ids.decorate(request, "xavaPropertiesList") + f;
	f++;
	String propertyI18n = Labels.getQualified(property, currentLocale);
	if (!Is.emptyString(searchWord) && !propertyI18n.toLowerCase().contains(searchWord)) continue;
	c++;
	if (tab.isColumnsToAddUntilSecondLevel() && c > 20) break;
%>
<tr id="<%=rowId%>" class="<%=cssClass%>" <%=events%>>
	<td class="<%=cssCellClass%>" width="5">
		<xava:action action='AddColumns.addColumn' argv='<%="property=" + property%>'/>
	</td>		
	<td class="<%=cssCellClass%>" width="5">
		<input class="xava_selected" type="checkbox" name="<xava:id name='xava_selected'/>" value="selectedProperties:<%=property%>"
			data-on-select-collection-element-action=""
			data-row="<%=f-1%>"
			data-view-object=""
			data-tab-object="<%=tabObject%>"
			data-confirm-message=""
			data-takes-long=""/>
	</td>
	<td class="<%=cssCellClass%>"> 
		<xava:link action='AddColumns.addColumn' argv='<%="property=" + property%>'>
		<div><%=propertyI18n%></div>
		</xava:link>
	</td>
</tr>
<%
}
%>

<%
if (tab.isColumnsToAddUntilSecondLevel()) {
%>
<tr class="ox-list-pair">
<td/>
<td/>
<td>
<xava:link action="AddColumns.showMoreColumns" cssClass="<%=style.getActionLink()%>"/> 
</td>
</tr>
<%
}
tab.setColumnsToAddUntilSecondLevel(originalColumnsToAddUntilSecondLevel);
%>

</table>
