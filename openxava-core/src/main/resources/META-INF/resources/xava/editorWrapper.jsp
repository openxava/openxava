<%@ include file="imports.jsp"%>

<xava:editor 
	property='<%=request.getParameter("propertyName")%>' 
	editable='<%=Boolean.valueOf(request.getParameter("editable")).booleanValue()%>' 
	throwPropertyChanged='<%=Boolean.valueOf(request.getParameter("throwPropertyChanged")).booleanValue()%>'/>
	
