<%@ include file="imports.jsp"%>

<%@ page import="java.util.Iterator" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.util.XavaPreferences" %>
<%@ page import="org.openxava.view.meta.MetaGroup" %>
<%@ page import="org.openxava.view.meta.PropertiesSeparator" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.model.meta.MetaReference" %>
<%@ page import="org.openxava.model.meta.MetaCollection" %>
<%@ page import="org.openxava.web.WebEditors" %>
<%@ page import="org.openxava.web.taglib.IdTag"%>
<%@ page import="org.openxava.web.Ids"%>
<%@ page import="org.openxava.model.meta.MetaMember"%>

<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%!
private boolean hasFrame(MetaMember m, View view) { 
	if (m instanceof MetaProperty) {
		return WebEditors.hasFrame((MetaProperty) m, view.getViewName());
	}
  	if (m instanceof MetaReference) {
  		return !view.displayReferenceWithNoFrameEditor((MetaReference) m);  		
  	}
  	return true;
}

private String openDivForFrame(View view) { 
	if (view.isFrame()) return openDiv(view);
	return "<div>" + openDiv(view);
}

private String closeDivForFrame(View view) { 
	if (view.isFrame()) return closeDiv(view);
	return closeDiv(view) + "</div>";
}

private String openDiv(View view) {
	if (view.isFlowLayout()) return ""; 
	return view.isFrame()?"<div class='ox-layout-detail'>":""; 
}

private String closeDiv(View view) {
	if (view.isFlowLayout()) return ""; 
	return view.isFrame()?"</div>":"";
}
%>

<%
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
view.setViewObject(viewObject); 
String propertyPrefix = request.getParameter("propertyPrefix");
String representsSection = request.getParameter("representsSection");
boolean isSection = "true".equalsIgnoreCase(representsSection);
propertyPrefix = (propertyPrefix == null || "null".equals(propertyPrefix))?"":propertyPrefix; 
view.setPropertyPrefix(propertyPrefix);
boolean onlySections = view.hasSections() && view.getMetaMembers().isEmpty(); 
%>

<%
if (!onlySections) {	// IF Not Only Sections
%>	
		<%=openDiv(view)%>	
<%
	Iterator it = view.getMetaMembers().iterator();
	String sfirst = request.getParameter("first");
	boolean first = !"false".equals(sfirst);
	while (it.hasNext()) {	// WHILE hasNext
		MetaMember m = (MetaMember) it.next();
		int frameWidth = view.isVariousMembersInSameLine(m)?0:100;
		if (m instanceof MetaProperty) {	// IF MetaProperty	
			MetaProperty p = (MetaProperty) m;		
			if (!PropertiesSeparator.INSTANCE.equals(m)) {	// IF Not Properties Separator	
				boolean hasFrame = WebEditors.hasFrame(p, view.getViewName());
				String propertyKey= Ids.decorate(
						request.getParameter("application"),
						request.getParameter("module"),
						propertyPrefix + p.getName());
				request.setAttribute(propertyKey, p);
				String urlEditor = "editor.jsp" // in this way because websphere 6 has problems with jsp:param
					+ "?propertyKey=" + propertyKey
					+ "&first=" + first
					+ "&hasFrame=" + hasFrame;		
				boolean withFrame = hasFrame && 
					(!view.isSection() || view.getMetaMembers().size() > 1);
				if (withFrame) { // IF MetaPropertt With Frame			 					
					String labelKey = Ids.decorate(
						request.getParameter("application"),
						request.getParameter("module"),
						"label_" + propertyPrefix + p.getName()); 
					String label = view.getLabelFor(p);
%>
			<%=closeDivForFrame(view)%> 
			<%=style.getFrameHeaderStartDecoration(frameWidth) %>
			<%=style.getFrameTitleStartDecoration() %>
			<span id="<%=labelKey%>"><%=label%></span>		
			<%=style.getFrameTitleEndDecoration() %>	
			<%=style.getFrameActionsStartDecoration()%>
<% 
					String frameId = Ids.decorate(request, "frame_" + view.getPropertyPrefix() + p.getName());
					String frameActionsURL = "frameActions.jsp?frameId=" + frameId +
						"&closed=" + view.isFrameClosed(frameId); 
%>
			<jsp:include page='<%=frameActionsURL%>'/>
			<%=style.getFrameActionsEndDecoration()%> 				
			<%@ include file="propertyActionsExt.jsp"%>					
			<%=style.getFrameHeaderEndDecoration() %>
			<%=style.getFrameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId))%>
<%	
				} // END MetaProperty With Frame 
%> 
			<jsp:include page="<%=urlEditor%>" />		
<%
				if (withFrame) { // IF MetaProperty With Frame
%>
			<%=style.getFrameContentEndDecoration() %>
			<%=openDivForFrame(view)%> 		
<%
				} // END IF MetaProperty With Frame		
				first = false;
			} // END IF Not Properties Separator
			else { // IF Properties Separator
				if (!it.hasNext()) break; 					
				first = true;						
%>
	<div class="ox-layout-new-line"></div> 			
<%		
			} // END IF Properties Separator
		} // END IF MetaProperty
		else { // IF Not MetaProperty
		  	if (m instanceof MetaReference) { // IF MetaReference
				MetaReference ref = (MetaReference) m;
				String referenceKey = Ids.decorate(
						request.getParameter("application"),
						request.getParameter("module"),
						propertyPrefix +  ref.getName()); 
				request.setAttribute(referenceKey, ref);
				if (view.displayReferenceWithNoFrameEditor(ref)) { // IF Display Reference Without Frame
					String urlReferenceEditor = "reference.jsp" // in this way because websphere 6 has problems with jsp:param
						+ "?referenceKey=" + referenceKey		
						+ "&first=" + first
						+ "&frame=false&composite=false&onlyEditor=false"; 				
%>
		<jsp:include page="<%=urlReferenceEditor%>"/>
<%
					first = false;		
				} // END IF Display MetaReference Without Frame
				else {	// IF Display MeteReference With Frame
					String viewName = viewObject + "_" + ref.getName();
					View subview = view.getSubview(ref.getName());
					context.put(request, viewName, subview);
					subview.setViewObject(viewName); 
					String propertyInReferencePrefix = propertyPrefix + ref.getName() + ".";
					boolean withFrame = subview.displayWithFrame(); 
					boolean firstForSubdetail = first || withFrame;
					if (withFrame) { // IF MetaReference With Frame					 					
						String labelKey = Ids.decorate(
							request.getParameter("application"),
							request.getParameter("module"),
							"label_" + propertyPrefix + ref.getName()); 
						String label = view.getLabelFor(ref);
%>				
		<%=closeDivForFrame(view)%> 
		<%=style.getFrameHeaderStartDecoration(frameWidth) %>
		<%=style.getFrameTitleStartDecoration() %>
		<span id="<%=labelKey%>"><%=label%></span>
		<% if (!ref.isAggregate()) { %>
		<jsp:include page="referenceFrameHeader.jsp"> 
			<jsp:param name="referenceName" value="<%=ref.getName()%>"/>
			<jsp:param name="viewObject" value="<%=viewObject%>"/>			
		</jsp:include>
		<% } %>
		<%=style.getFrameTitleEndDecoration() %>
		<%=style.getFrameActionsStartDecoration()%>
<% 
						String frameId = Ids.decorate(request, "frame_" + view.getPropertyPrefix() + ref.getName());
						String frameActionsURL = "frameActions.jsp?frameId=" + frameId +
							"&closed=" + view.isFrameClosed(frameId); 		
%>
		<jsp:include page='<%=frameActionsURL%>'/>
		<%=style.getFrameActionsEndDecoration()%>
		<%@ include file="referenceFrameHeaderExt.jsp"%>				 					
		<%=style.getFrameHeaderEndDecoration() %>
		<%=style.getFrameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId)) %>						
<%		
					} // END IF MetaReference With Frame
			
					String urlReferenceEditor = null;
					if (view.displayReferenceWithNotCompositeEditor(ref)) { // IF Display Reference Without Composite Editor
						urlReferenceEditor = "reference.jsp" // in this way because websphere 6 has problems with jsp:param					
							+ "?referenceKey=" + referenceKey
							+ "&onlyEditor=true&frame=true&composite=false"		
							+ "&first=" + first;				
					} // END IF Display Reference Without Composite Editor
					else { // IF Display Reference With Composite Editor						
						urlReferenceEditor = "reference.jsp" // in this way because websphere 6 has problems with jsp:param
							+ "?referenceKey=" + referenceKey
							+ "&onlyEditor=true&frame=true&composite=true"  
							+ "&refViewObject=" + viewName  
							+ "&propertyPrefix=" + propertyInReferencePrefix 
							+ "&first=" + firstForSubdetail;
					} // END IF Display Reference With Composite Editor		
%>  
		<jsp:include page="<%=urlReferenceEditor%>"/>
<%
					if (withFrame) { // IF MetaReference With Frame
%>			
		<%=style.getFrameContentEndDecoration() %>
		<%=openDivForFrame(view)%> 
<%
					} // END IF MetaReference With Frame
				} // END Display MetaReference With Frame
				first = false; 
			} else if (m instanceof MetaCollection) { // IF MetaCollection
				MetaCollection collection = (MetaCollection) m;			
				boolean withFrame = !view.isSection() || view.getMetaMembers().size() > 1;
				boolean variousCollectionInLine = view.isVariousCollectionsInSameLine((MetaMember) m);
%>
			<%=closeDivForFrame(view)%>
<%	
				if (withFrame) { // IF MetaCollection With Frame
%>	
				<%=style.getCollectionFrameHeaderStartDecoration(variousCollectionInLine?50:frameWidth)%>
				<%=style.getFrameTitleStartDecoration()%>
				<%=collection.getLabel(request) %>
<% 
				String frameId = Ids.decorate(request, "frame_" + view.getPropertyPrefix() + collection.getName());
				String colletionHeaderId = frameId + "header";
%>				
				<span id="<xava:id name='<%=colletionHeaderId%>'/>">
					<jsp:include page="collectionFrameHeader.jsp"> 
						<jsp:param name="collectionName" value="<%=collection.getName()%>"/>
						<jsp:param name="viewObject" value="<%=viewObject%>"/>			
					</jsp:include>			
				</span>	
				<%=style.getFrameTitleEndDecoration()%>
				<%=style.getFrameActionsStartDecoration()%>
<% 
				String frameActionsURL = "frameActions.jsp?frameId=" + frameId +
					"&closed=" + view.isFrameClosed(frameId);
%>
				<jsp:include page='<%=frameActionsURL%>'/>
				<%=style.getFrameActionsEndDecoration()%> 	
				<%@ include file="collectionFrameHeaderExt.jsp"%>				 					
				<%=style.getFrameHeaderEndDecoration()%>
				<%=style.getFrameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId))%>
<%
				} // END IF MetaCollection With Frame
%>	
				<%
				String collectionPrefix = propertyPrefix == null?collection.getName() + ".":propertyPrefix + collection.getName() + ".";
				%>
				<div id="<xava:id name='<%="collection_" + collectionPrefix%>'/>">				
					<jsp:include page="collection.jsp"> 
						<jsp:param name="collectionName" value="<%=collection.getName()%>"/>
						<jsp:param name="viewObject" value="<%=viewObject%>"/>			
					</jsp:include>
				</div>				
<%			
				if (withFrame) { // IF MetaCollection With Frame
%>
				<%=style.getFrameContentEndDecoration()%>			
<%
				} // END IF MetaCollection With Frame
%>
			<%=openDivForFrame(view)%> 
<%
			} else if (m instanceof MetaGroup) { // IF MetaGroup
				MetaGroup group = (MetaGroup) m;			
				String viewName = viewObject + "_" + group.getName();
				View subview = view.getGroupView(group.getName());			
				context.put(request, viewName, subview);
%>
			<%=closeDivForFrame(view)%> 
			<%=style.getFrameHeaderStartDecoration(frameWidth)%>
			<%=style.getFrameTitleStartDecoration()%>
			<% String labelId = Ids.decorate(request, "label_" + view.getPropertyPrefix() + group.getName()); %>
			<span id="<%=labelId%>"><%=group.getLabel(request)%></span>
			<%=style.getFrameTitleEndDecoration()%>
			<%=style.getFrameActionsStartDecoration()%>
<% 
				String frameId = Ids.decorate(request, "frame_group_" + view.getPropertyPrefix() + group.getName());
				String frameActionsURL = "frameActions.jsp?frameId=" + frameId + 
					"&closed=" + view.isFrameClosed(frameId); 
%>
			<jsp:include page='<%=frameActionsURL%>'/>
			<%=style.getFrameActionsEndDecoration()%> 					 			
			<%=style.getFrameHeaderEndDecoration()%>
			<%=style.getFrameContentStartDecoration(frameId + "content", view.isFrameClosed(frameId)) %>
			<% if (view.isFlowLayout()) { %> 
			<div class='ox-flow-layout'>
			<% } %>
			<jsp:include page="detail.jsp">
				<jsp:param name="viewObject" value="<%=viewName%>" />
			</jsp:include>
			<% if (view.isFlowLayout()) { %> 
			</div>
			<% } %>
			<%=style.getFrameContentEndDecoration() %>
			<%=openDivForFrame(view)%> 
<%
			} // END IF MetaGroup
		} // END IF Not MetaProperty
	} // END While hasNext
%>

<%=closeDiv(view)%>

<% 
} // END if (!onlySections) {
%>

<%
if (view.hasSections()) { // IF Has Sections
%>
<div id="<xava:id name='<%="sections_" + viewObject%>'/>" class="<%=style.getSections()%>">
	<jsp:include page="sections.jsp"/>
</div>	
<% 
}
%>
