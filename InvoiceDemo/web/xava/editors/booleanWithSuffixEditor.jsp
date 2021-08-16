<%@ include file="../imports.jsp"%>
<%@ include file="booleanEditor.jsp"%>

<span class="<%=style.getEditorSuffix()%>"> 
<xava:message key='<%=request.getParameter("suffix")%>'/>
</span>