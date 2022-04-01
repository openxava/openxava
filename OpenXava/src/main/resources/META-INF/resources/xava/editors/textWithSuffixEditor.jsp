<%@ include file="../imports.jsp"%>
<%@ include file="textEditor.jsp"%>
 
<span class="<%=style.getEditorSuffix()%>">  
<xava:message key='<%=request.getParameter("suffix")%>'/>
</span>