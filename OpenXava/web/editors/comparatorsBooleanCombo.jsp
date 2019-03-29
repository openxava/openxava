<%@ include file="../imports.jsp"%>

<%@page import="org.openxava.tab.Tab"%>
<%@page import="org.openxava.util.Is"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String comparator = request.getParameter("comparator");
String equal = Tab.EQ_COMPARATOR.equals(comparator)?"selected='selected'":"";
String different = Tab.NE_COMPARATOR.equals(comparator)?"selected='selected'":"";
String prefix = request.getParameter("prefix");
if (prefix == null) prefix = "";
int index = Integer.parseInt(request.getParameter("index"));
boolean filterOnChange = org.openxava.util.XavaPreferences.getInstance().isFilterOnChange();
String collection = request.getParameter("collection"); 
String collectionArgv = Is.emptyString(collection)?"":"collection="+collection;
%>
<div>
	<input type="hidden" name="<xava:id name='<%=prefix + "conditionValue." + index%>'/>" value="true">
	<input type="hidden" name="<xava:id name='<%=prefix + "conditionValueTo." + index%>'/>" >
	<!-- conditionValueTo: we need all indexes to implement the range filters -->
</div>

<select name="<xava:id name='<%=prefix + "conditionComparator."  + index%>'/>" style="width: 100%;" class=<%=style.getEditor()%>
<% if(filterOnChange) { %>
	onchange="openxava.executeAction('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '', false, 'List.filter','<%=collectionArgv%>')"
<% } %>
>
	<option value=""></option>
	<option value="<%=Tab.EQ_COMPARATOR%>" <%=equal%>><xava:message key="yes"/></option>
	<option value="<%=Tab.NE_COMPARATOR%>" <%=different%>><xava:message key="no"/></option>
</select>	
	