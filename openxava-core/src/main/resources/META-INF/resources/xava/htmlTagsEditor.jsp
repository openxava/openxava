<%
String sfirst = request.getParameter("first"); 
boolean first="true".equals(sfirst)?true:false;

String labelClass = null;
String editorClass = null;

if (view.isAlignedByColumns()) {
	labelClass = editorClass = "ox-layout-aligned-cell";
}
else {
	editorClass = "ox-layout-not-aligned-cell";
	labelClass = first?"ox-layout-aligned-cell":"ox-layout-not-aligned-cell";
}

String preLabel="<div class='" + labelClass + " " + style.getLabel() + "'>";
String postLabel="</div>";
String preEditor="<div class='" + editorClass + " " + style.getEditorWrapper()+ "'>";
String postEditor="</div>";
%>