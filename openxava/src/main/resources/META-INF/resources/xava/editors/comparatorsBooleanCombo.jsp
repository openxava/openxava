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
String collection = request.getParameter("collection"); 
String collectionArgv = Is.emptyString(collection)?"":"collection="+collection;
%>
<div>
	<%-- id needed in order openxava.renumberListColumns() works  --%>
	<input id="<xava:id name='<%=prefix + "conditionValue." + index%>'/>" type="hidden" name="<xava:id name='<%=prefix + "conditionValue." + index%>'/>" value="true">
	<input id="<xava:id name='<%=prefix + "conditionValueTo." + index%>'/>" type="hidden" name="<xava:id name='<%=prefix + "conditionValueTo." + index%>'/>" >
	<%-- conditionValueTo: we need all indexes to implement the range filters --%>
</div>

<select name="<xava:id name='<%=prefix + "conditionComparator."  + index%>'/>" class="xava_combo_condition_value <%=style.getEditor()%>"
	data-collection-argv="<%=collectionArgv%>">
	<option value=""></option>
	<option value="<%=Tab.EQ_COMPARATOR%>" <%=equal%>><xava:message key="yes"/></option>
	<option value="<%=Tab.NE_COMPARATOR%>" <%=different%>><xava:message key="no"/></option>
</select>	
	