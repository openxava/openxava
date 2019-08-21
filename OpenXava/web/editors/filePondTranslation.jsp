<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.util.Locales"%>

<% 
if (!Locales.getCurrent().getLanguage().equals("en") && request.getAttribute("xava.filepond.transaled") == null) {  
%>
	<script>
	FilePond.setOptions({
		labelIdle: '<xava:message key="filepond_labelIdle"/>',
		labelFileWaitingForSize: '<xava:message key="filepond_labelFileWaitingForSize"/>',
		labelFileSizeNotAvailable: '<xava:message key="filepond_labelFileSizeNotAvailable"/>',
		labelFileLoading: '<xava:message key="loading"/>', 
		labelFileLoadError: '<xava:message key="filepond_labelFileLoadError"/>',
		labelFileProcessing: '<xava:message key="file_uploading"/>', 
		labelFileProcessingComplete: '<xava:message key="filepond_labelFileProcessingComplete"/>',
		labelFileProcessingAborted: '<xava:message key="filepond_labelFileProcessingAborted"/>',
		labelFileProcessingError: '<xava:message key="filepond_labelFileProcessingError"/>',
		labelFileProcessingRevertError: '<xava:message key="filepond_labelFileProcessingRevertError"/>',
		labelFileRemoveError: '<xava:message key="filepond_labelFileRemoveError"/>',
		labelTapToCancel: '<xava:message key="filepond_labelTapToCancel"/>',
		labelTapToRetry: '<xava:message key="filepond_labelTapToRetry"/>',
		labelTapToUndo: '<xava:message key="filepond_labelTapToUndo"/>',
		labelButtonRemoveItem: '<xava:label key="remove"/>', 
		labelButtonAbortItemLoad: '<xava:label key="abort"/>', 
		labelButtonRetryItemLoad: '<xava:label key="retry"/>', 
		labelButtonAbortItemProcessing: '<xava:label key="cancel"/>', 
		labelButtonUndoItemProcessing: '<xava:label key="undo"/>', 
		filepond_labelButtonRetryItemProcessing: '<xava:label key="retry"/>', 
		filepond_labelButtonProcessItem: '<xava:label key="upload"/>' 
	});
	</script>
<%
	request.setAttribute("xava.filepond.transaled", true);
} 
%> 	
