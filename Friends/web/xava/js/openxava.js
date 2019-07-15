if (openxava == null) var openxava = {};
openxava.deselected = [];

openxava.init = function(application, module, initUI) { 
	openxava.initWindowId(); 
	document.onkeydown = openxava.processKey;
	if (initUI) openxava.initUI(application, module); 
	openxava.editorsInitFunctionsClosed = true; 
	if (openxava.staticInit == null) {
		openxava.staticInit = function() {			  
			  openxava.initEditors();
		}
		$(openxava.staticInit);
	}
	openxava.initStrokeActions(application, module); 
}

openxava.ajaxRequest = function(application, module, firstRequest, inNewWindow) {
	if (openxava.isRequesting(application, module)) return;
	openxava.setRequesting(application, module);
	document.throwPropertyChange = false;
	openxava.getElementById(application, module, "loading").value=true;
	document.body.style.cursor='wait';
	if (!$('#xava_loading').is(':visible')) openxava.fadeIn('#xava_loading', 1000); 
	if (!$('#xava_loading2').is(':visible')) openxava.fadeIn('#xava_loading2', 1000); 
		
	if (inNewWindow) {
		openxava.newWindow = window.open('', '_blank'); 
	}
	Module.request(
			application, module, document.additionalParameters,			
			openxava.getFormValues(openxava.getForm(application, module)), 
			openxava.getMultipleValues(application, module), 
			openxava.getSelectedValues(application, module),
			openxava.deselected,
			firstRequest,
			openxava.baseFolder, 
			openxava.refreshPage);	
	$(window).unbind('resize');
	openxava.deselected = [];
}

openxava.refreshPage = function(result) {
	var changed = "";	
	if (result.error != null) {		
		openxava.systemError(result);
		changed = " ERROR";
		return;
	}
	if (result.reload) {
		window.location.reload();
		return;
	}		
	if (result.forwardURL != null) {
		if (result.forwardInNewWindow) { 
			if (openxava.newWindow != null) {
				openxava.newWindow.location = result.forwardURL;
				openxava.newWindow = null;
			}
			else {
				window.open(result.forwardURL);
			}
			var form = openxava.getForm(result.application, result.module);
			if (form != null) { 
				form[openxava.decorateId(result.application, result.module, "xava_action")].value="";	
				form[openxava.decorateId(result.application, result.module, "xava_action_argv")].value="";
				form[openxava.decorateId(result.application, result.module, "xava_changed_property")].value="";
				form[openxava.decorateId(result.application, result.module, "xava_action_range")].value="";
			}
			openxava.setUrlParam(""); 
			window.location.reload();	
			return; 			
		}
		else {
			location.href=result.forwardURL;			
		}
	}
	else if (result.forwardURLs != null) { 
		for (var i=0; i<result.forwardURLs.length; i++) {
			window.open(result.forwardURLs[i]);
			var form = openxava.getForm(result.application, result.module);
			if (form != null) { 
				form[openxava.decorateId(result.application, result.module, "xava_action")].value="";	
				form[openxava.decorateId(result.application, result.module, "xava_action_argv")].value="";
				form[openxava.decorateId(result.application, result.module, "xava_changed_property")].value="";
				form[openxava.decorateId(result.application, result.module, "xava_action_range")].value="";
			}
		}
		window.location.reload(); 
		return; 				
	}	
	else if (result.nextModule != null) {	
		openxava.updateRootIds(result.application, result.module, result.nextModule);
		document.getElementById("xava_last_module_change").value=result.module + "::" + result.nextModule;		
		if (openxava.dialogLevel > 0) openxava.closeDialog(result);							
		openxava.resetRequesting(result); 
		openxava.ajaxRequest(result.application, result.nextModule); 
		return;
	}	
	else {		
		openxava.destroyEditors(); // Before closeDialog() to avoid an error on closing a dialog with a CKEditor
		if (result.showDialog){	
			openxava.disableElements(result);
		}
		else if (result.hideDialog) {
			openxava.closeDialog(result); 
		}
		openxava.dialogLevel = result.dialogLevel;
		var dialog;
		if (result.showDialog || result.resizeDialog) { 
			dialog = openxava.getDialog(result.application, result.module);
		}
		openxava.strokeActions = result.strokeActions;
		var changedParts = result.changedParts;
		for (var id in changedParts) {			
			changed = changed + id + ", ";  			
			try {
				openxava.setHtml(id, changedParts[id]);
			}
			catch (ex) {
				changed = changed + " ERROR";
				alert("Error refreshing part: " + id);
				errors = true;
				break;
			}			
		}  
		if (result.showDialog){
			openxava.initBeforeShowDialog(); 
			dialog.attr("application", result.application);
			dialog.attr("module", result.module);
			dialog.dialog('option', 'title', result.dialogTitle);
			dialog.dialog('option', 'width', 'auto');
			var maxWidth = $(window).width() * 0.95; 
			var width = dialog.parent().width();
			dialog.dialog('option', 'width', Math.min(width, maxWidth));
			dialog.dialog('widget').css('min-width', 'fit-content'); 
			dialog.dialog('widget').css('margin-left', '20px'); 
			dialog.dialog('widget').css('margin-right', '20px'); 
			dialog.dialog('option', 'height', 'auto');
			dialog.dialog('option', 'position', { my: "center", at: "center", of: window, collision: "fit" } ); 			
			dialog.dialog('option', 'zIndex', 99999 );
			if (result.dialogTitle.indexOf("!x:") === 0) {
				dialog.dialog('option', 'title', result.dialogTitle.substring(3));
				dialog.dialog('option', 'dialogClass', 'no-close');
				dialog.dialog('option', 'closeOnEscape', false);
			}
			dialog.dialog('open');
		}
		else if (result.resizeDialog) {
			if (dialog.dialog('isOpen')) dialog.dialog('close'); 
			if (result.dialogTitle) dialog.dialog('option', 'title', result.dialogTitle);
			dialog.dialog('option', 'width', 'auto');
			dialog.dialog('option', 'width', dialog.parent().width());
			dialog.dialog('open');					
		}
		openxava.selectRows(result.application, result.module, result.selectedRows);
		openxava.initUI(result.application, result.module, result.currentRow, result.viewSimple);
		openxava.stylizeEditorsWithError(result.application, result.module, result.editorsWithError, result.editorsWithoutError); 
		if (result.focusPropertyId != null) { 
			openxava.getElementById(result.application, result.module, "xava_focus_property_id").value = result.focusPropertyId;
			openxava.setFocus(result.application, result.module);
		}			
	}		
	if (openxava.newWindow != null) {
		openxava.newWindow.close();
		openxava.newWindow = null; 
	}
	document.getElementById('xava_processing_layer').style.display='none';
	var form = openxava.getForm(result.application, result.module);	
	if (form != null) {  
		form[openxava.decorateId(result.application, result.module, "xava_action")].value=""; 
		form[openxava.decorateId(result.application, result.module, "xava_action_argv")].value="";
		form[openxava.decorateId(result.application, result.module, "xava_changed_property")].value="";
		form[openxava.decorateId(result.application, result.module, "xava_action_range")].value="";		
	}	
	openxava.getElementById(result.application, result.module, "loaded_parts").value=changed;
	openxava.getElementById(result.application, result.module, "loading").value=false;
	openxava.getElementById(result.application, result.module, "view_member").value=result.viewMember; 
	openxava.lastApplication=result.application;
	openxava.lastModule=result.module;
	openxava.hasOnSelectAll(result.application, result.module);
	openxava.showMessages(result); 
	openxava.resetRequesting(result);
	openxava.propertiesUsedInCalculationsChange(result);
	$('#xava_loading').hide();
	$('#xava_loading2').hide();
	if (result.postJS != null) {
		eval(result.postJS);
	}
	document.body.style.cursor='auto';
	if (openxava.postRefreshPage != null) openxava.postRefreshPage(); 
	openxava.setUrlParam(result.urlParam); 
}

openxava.initUI = function(application, module, currentRow, viewSimple) { 
	if (openxava.initTheme != null) openxava.initTheme();
	openxava.clearLists(application, module); 
	openxava.initLists(application, module);
	if (typeof currentRow != "undefined") {
		openxava.initEditors(); 
	}
	openxava.initSelectedRows();
	openxava.initCurrentRow(application, module, currentRow);
	openxava.initViewSimple(application, module, viewSimple);
	openxava.initTooltips();
	openxava.initPlaceholder();
}

openxava.stylizeEditorsWithError = function(application, module, editorsWithError, editorsWithoutError) { 
	for (var i in editorsWithoutError) {
		var id = openxava.decorateId(application, module, editorsWithoutError[i]); 
		$('#' + id).removeClass(openxava.errorEditorClass); 
	}	
	for (var i in editorsWithError) {
		var id = openxava.decorateId(application, module, editorsWithError[i]); 
		$('#' + id).addClass(openxava.errorEditorClass); 
	}
}

openxava.initPlaceholder = function(){
	$(".xava_editor[data-placeholder]").each(function() {
		$(this).find("input:visible:first,textarea:visible:first").attr("placeholder", $(this).data("placeholder")); 
	});
}

openxava.initTooltips = function() { 
	$(".xava_editor").find("input").tooltip({ 
		position: {
			my: "left+3 center", at: "right center", 
	        using: function( position, feedback ) {
			$( this ).css( position );
			$( "<div>" )
				.addClass( "arrow" )
				.addClass( feedback.vertical )
				.addClass( feedback.horizontal )
				.appendTo( this );
	        }
		}
	});	
}

openxava.initViewSimple = function(application, module, viewSimple) { 
	if (typeof viewSimple != "undefined") {
		if (viewSimple) $('#' + openxava.decorateId(application, module, 'view')).addClass("ox-simple-layout");
		else $('#' + openxava.decorateId(application, module, 'view')).removeClass("ox-simple-layout");
	}	
}

openxava.initStrokeActions = function(application, module) { 
	Module.getStrokeActions(application, module, openxava.setStrokeActions);
}

openxava.initBeforeShowDialog = function() { 
	$("#xava_add_columns").css("max-height", $(window).height() * 0.7); 
}

openxava.initWindowId = function() { 
	$(window).bind('beforeunload',function(){
		document.cookie = "XAVA_WINDOW_ID=" + $("#xava_window_id").val(); 
	});		
	document.cookie="XAVA_WINDOW_ID=";   
	dwr.engine.setHeaders({ xava_window_id: $("#xava_window_id").val() }); 
}

openxava.selectRows = function(application, module, selectedRows) {
	if (selectedRows == null) return;	
	for (collection in selectedRows) {
		$("#" + openxava.decorateId(application, module, collection))
			.find("._XAVA_SELECTED_ROW_")
				.removeClass("_XAVA_SELECTED_ROW_ " + openxava.selectedRowClass)
				.find('[type="checkbox"]').prop("checked", false);		 		
		for (i in selectedRows[collection]) {
			var row = selectedRows[collection][i];
			var id = "xava_collectionTab_" + collection + "_" + row;
			$("#" + openxava.decorateId(application, module, "xava_collectionTab_" + collection + "_" + row))
				.addClass("_XAVA_SELECTED_ROW_")
				.find('[type="checkbox"]').prop("checked", true);									
		}		
	}		
}

openxava.setStrokeActions = function(strokeActions) { 
	if (strokeActions == null) window.location.reload(); 
	else openxava.strokeActions = strokeActions;
}

openxava.propertiesUsedInCalculationsChange = function(result) { 
	if (result.propertiesUsedInCalculations != null) {
		for (var i=0; i<result.propertiesUsedInCalculations.length; i++) {
			$('#' + openxava.decorateId(result.application, result.module, result.propertiesUsedInCalculations[i])).change();
			if (/_SUM_$/.test(result.propertiesUsedInCalculations[i])) {
				openxava.executeAction(result.application, result.module, false, false, "CollectionTotals.save", "sumProperty=" + result.propertiesUsedInCalculations[i]);
			}
		}
	}	
}

openxava.showMessages = function(result) { 
	var messagesIsEmpty = openxava.getElementById(result.application, result.module, "messages_table") == null;
	var errorsIsEmpty = openxava.getElementById(result.application, result.module, "errors_table") == null;
	if (!messagesIsEmpty) openxava.effectShow(result.application, result.module, "messages");
	if (!errorsIsEmpty) openxava.effectShow(result.application, result.module, "errors");
}

openxava.initSelectedRows = function() { 
	$("._XAVA_SELECTED_ROW_").addClass(openxava.selectedRowClass);
}

openxava.initCurrentRow = function(application, module, currentRow) {
	$("._XAVA_CURRENT_ROW_")
		.removeClass("_XAVA_CURRENT_ROW_")
		.removeClass(openxava.currentRowClass)
		.children()
			.removeClass(openxava.currentRowCellClass);
	if (currentRow == null) return;
	var id = openxava.decorateId(application, module, "" + currentRow);		
	$("#" + id).addClass("_XAVA_CURRENT_ROW_").addClass(openxava.currentRowClass).
		children().addClass(openxava.currentRowCellClass);
}

openxava.markRows = function() { 	
	$("._XAVA_CURRENT_ROW_").addClass(openxava.currentRowClass)
		.children().addClass(openxava.currentRowCellClass); 
	openxava.initSelectedRows();
}

openxava.disableElements = function(result) {	
	var rawRootId = openxava.dialogLevel > 0?"dialog" + openxava.dialogLevel:"core"; 
	var rootId = openxava.decorateId(result.application, result.module, rawRootId); 
	var prefixId = openxava.decorateId(result.application, result.module, "");
	$("#" + rootId).find("[id^=" + prefixId + "]").each(
		function () {			
			this.id = this.id + "__DISABLED__";  
		}
	);
	$("#" + rootId).find("[name^=" + prefixId + "]").each(
		function () {			
			this.name = this.name + "__DISABLED__"; 
		}	
	);	
	openxava.disableScrolls(result, rootId, "list"); // Solves bug 395													 
	openxava.disableScrolls(result, rootId, "collection"); 
}

openxava.disableScrolls = function(result, rootId, type) { 
	var scrollClass = openxava.decorateId(result.application, result.module, type + "_scroll");	
	$("#" + rootId).find("[class=" + scrollClass + "]").each(
		function () {			
			this.className = this.className + "__DISABLED__"; 
		}	
	);	
}

openxava.closeDialog = function(result) {
	var dialog = openxava.getDialog(result.application, result.module);
	dialog.attr("application", ""); 
	dialog.attr("module", "");
	dialog.dialog('close');
	dialog.empty(); 
}

openxava.onCloseDialog = function(event) {  
	var dialog = $(event.target); 
	var application = dialog.attr("application");
	if (application && application != "") { 
		var module = dialog.attr("module");
		openxava.executeAction(application, module, false, false, "Dialog.cancel");
		return;
	}	
	openxava.clearLists(); 
	dialog.empty();
}


openxava.updateRootIds = function(application, moduleFrom, moduleTo) { 
	openxava.getElementById(
		application, moduleFrom, "loading").id =	
			openxava.decorateId(application, moduleTo, "loading");
	openxava.getElementById(
		application, moduleFrom, "core").id =	
			openxava.decorateId(application, moduleTo, "core");
	openxava.getElementById(
		application, moduleFrom, "loaded_parts").id =	
			openxava.decorateId(application, moduleTo, "loaded_parts");
	openxava.getElementById( 
			application, moduleFrom, "view_member").id =	
				openxava.decorateId(application, moduleTo, "view_member");	
}

openxava.initLists = function(application, module) {  
	$(".xava_resizable").resizable({
		handles: 'e',
		resize: function(event, ui) { 
			var newWidth = $(event.target).width() - 1;
			$(event.target).parent().width(newWidth);
			$("." + event.target.id).width(newWidth);
		},
		stop: function(event, ui) {			
			Tab.setColumnWidth(event.target.id, $(event.target).closest("th").index() - 2, $(event.target).width());
		}
	});				
	openxava.setListsSize(application, module, "list", openxava.listAdjustment); 
	openxava.setListsSize(application, module, "collection", openxava.collectionAdjustment);
	$('.xava_sortable_column').sorttable({ 
		placeholder: 'xava_placeholder',
	    helperCells: null,
	    items: '>:gt(1)',
	    handle: ".xava_handle",
	    start: function( event, ui ) {	    	
	    	ui.helper.addClass("xava_dropped");
	    	ui.item.startPos = ui.item.index(); 
	    },
	    stop: function( event, ui ) {
	    	ui.item.css("width", "");
	    	var tableId = $(event.target).closest("table").attr("id"); 
	    	Tab.moveProperty(tableId, ui.item.startPos - 2, ui.item.index() - 2);
	    }
	});
	$('.xava_sortable_row').sortable({ 
		items: '>:gt(0)',
		handle: ".xava_handle", 
	    start: function( event, ui ) {	    	
	    	ui.item.startPos = ui.item.index(); 
	    },		
	    stop: function( event, ui ) {
	    	var tableId = $(event.target).closest("table").attr("id");
	    	View.moveCollectionElement(tableId, ui.item.startPos - 1, ui.item.index() - 1);
	    }	
	});
	openxava.watchColumnsSearch();
	$('.xava_filter input').keypress(function() { 
		$(this).parent().parent().find(".xava_comparator").fadeIn();
	});
	$('.xava_filter input').change(function() { // If changed, revise ModuleTestBase.setCollectionCondition()
		$(this).parent().parent().find(".xava_comparator").fadeIn();
	});
}

openxava.setListsSize = function(application, module, type, adjustment) {
	var buttonBar = $('#' + openxava.decorateId(application, module, "bottom_buttons"));
	var scrollId = '.' + openxava.decorateId(application, module, type + "_scroll");
	if (openxava.dialogLevel > 0) scrollId = ".ui-dialog " + scrollId; // To avoid that showing a dialog resizes the list
	$(scrollId).width(50); 	  
	$(scrollId).width(buttonBar.outerWidth() + adjustment); 
	$(window).resize(function() {
		$(scrollId).width(50); 
		$(scrollId).width(buttonBar.outerWidth() + adjustment); 		
	});
}

openxava.addEditorDestroyFunction = function(destroyFunction) { 
	if (openxava.editorsDestroyFunctionsClosed) return; 
	if (openxava.editorsDestroyFunctions == null) {
		openxava.editorsDestroyFunctions = new Array();	
	}
	openxava.editorsDestroyFunctions.push(destroyFunction);	
}

openxava.addEditorInitFunction = function(initFunction) {
	if (openxava.editorsInitFunctionsClosed) return; 
	if (openxava.editorsInitFunctions == null) {
		openxava.editorsInitFunctions = new Array();	
	}
	openxava.editorsInitFunctions.push(initFunction);	
}

openxava.destroyEditors = function() {   
	if (openxava.editorsDestroyFunctions == null) return;	
	for (var i in openxava.editorsDestroyFunctions) {
		openxava.editorsDestroyFunctions[i]();
	}
}

openxava.initEditors = function() { 
	if (openxava.editorsInitFunctions == null) return;	
	for (var i in openxava.editorsInitFunctions) {
		openxava.editorsInitFunctions[i]();
	}
}

openxava.clearLists = function(application, module) { 
	$('.qtip').hide();
}

openxava.getDialog = function(application, module) {  
	if (openxava.dialogs == null) openxava.dialogs = { };
	var dialogId = openxava.decorateId(application, module, "dialog" + openxava.dialogLevel);
	var dialog = openxava.dialogs[dialogId];
	if (dialog == null) {
		$("body").append("<div id='" + openxava.decorateId(application, module , 
				"dialog" + openxava.dialogLevel) + "'></div>");
		dialog = $('#' + dialogId).dialog({
			autoOpen: false,
			modal: true,
			resizable: true,
			width: 'auto',
			height: 'auto',
			bgiframe: true,
			close: openxava.onCloseDialog,
			closeOnEscape: openxava.closeDialogOnEscape 
		});
		openxava.dialogs[dialogId] = dialog;		
	}
	return dialog;
}

openxava.setUrlParam = function(urlParam) { 
	if (urlParam == null) return;
	if (urlParam !== "") {
		var url = window.location.href;
		var indexParams = url.indexOf('?');
		if (indexParams >= 0) url = url.substring(0, indexParams);
		history.replaceState(null, null, url + "?" + urlParam);
	}
	else {
		history.replaceState(null, null, window.location.pathname);
	}		
}


openxava.setRequesting = function(application, module) {
	if (openxava.requesting == null) openxava.requesting = { };
	openxava.requesting[application + "::" + module] = true;
}

openxava.isRequesting = function(application, module) {
	if (openxava.requesting == null) return false;
	return openxava.requesting[application + "::" + module];
}

openxava.resetRequesting = function(result) {
	if (openxava.requesting == null) return;
	openxava.requesting[result.application + "::" + result.module] = false;
}

openxava.getElementById = function(application, module, id) {
	return $("#" + openxava.decorateId(application, module, id))[0]; 
}

openxava.getForm = function(application, module) {
	return openxava.getElementById(application, module, "form");
}

// The logic is the same of org.openxava.web.Ids.decorateId 
openxava.decorateId = function(application, module, simpleName) { 	
	return "ox_" + application + "_" + module + "__" + simpleName.replace(/\./g, "___"); 
}

openxava.systemError = function(result) { 
	document.body.style.cursor='auto';	
	openxava.getElementById(result.application, result.module, "core").innerHTML="<big id='xava_system_error'><big style='padding: 5px;font-weight: bold; color: rgb(255, 0, 0);'>ERROR: " + result.error + "</big></big>";
}

openxava.processKey = function(event) {	
	if (!event) event = window.event;
	
	if (event.keyCode == 13 ) {
		var textField = $(event.target);
		var id = $(textField).attr("id");
		if (/.*_conditionValue___\d+$/.test(id)) {
			event.returnValue = false;
			event.preventDefault();
			
			var collection = ""; 
			if (id.indexOf("_xava_collectionTab_") >= 0) { 
				var i = 6;
				var tokens = id.split("_");
				while (tokens[i] !== "conditionValue" && i < tokens.length) {
					if (collection !== "") collection = collection + "_";  
					collection = collection + tokens[i];
					i++;
				}
			}

			openxava.executeAction(openxava.lastApplication, openxava.lastModule,
				"", false, "List.filter", "collection=" + collection);
		}
		return;
	}
	
	if ( !(event.keyCode >= 112 && event.keyCode <= 123 ||
			event.ctrlKey || event.altKey || event.shiftKey) ) return;
	
	if (event.keyCode >= 49 && event.keyCode <= 57 && event.ctrlKey && !event.altKey) {				
		event.returnValue = false;
		event.preventDefault();
		openxava.executeAction(openxava.lastApplication, openxava.lastModule,
			"", false, "Sections.change", "activeSection=" + (event.keyCode - 49)); 
		return;
	}	
	
	var key = event.keyCode + "," + event.ctrlKey + "," + event.altKey + "," + event.shiftKey;	
	var action = openxava.strokeActions[key];
	if (action != null) {				
		event.returnValue = false;
		event.preventDefault();
		openxava.executeAction(openxava.lastApplication, openxava.lastModule,
			action.confirmMessage, action.takesLong, action.name);  
		return;
	}	
}

openxava.getSelectedValues = function(application, module) {  	  		
	var result = new Array();
	var selected = document.getElementsByName(openxava.decorateId(application, module, "xava_selected"));  
	var j=0;
	for (var i=0; i<selected.length; i++) {
  		if (selected[i].checked) {
	  		result[j++] = selected[i].value;
  		}
	}	  		
	return result;
}

openxava.getMultipleValues = function(application, module) { 
	var result = new Object();
	var multiple = document.getElementsByName(openxava.decorateId(application, module, "xava_multiple"));  
	for (var i=0; i<multiple.length; i++) {
  		var propertyName = multiple[i].value; 
  		var elements = document.getElementsByName(propertyName);
  		if (elements.length == 1) {
  			result[propertyName] = dwr.util.toDescriptiveString(dwr.util.getValue(propertyName), 2);
  		}
  		else {  	  			
  			for (var j=0; j<elements.length; j++) {
  				var indexedName = elements[j].name + "::" + j;
  				var originalName = elements[j].name;
  				var element = elements[j]; 
  				element.name = indexedName;
  				result[indexedName] = openxava.getFormValue(element);
  				element.name = originalName;  				  				
  			}
  		}
	}	  		
	return result;
}

// JavaScript for text area maxsize
openxava.limitLength = function(ev, max) { 
	var target = window.event ? window.event.srcElement : ev.target;			
	if ( target.value.length > max ) {
		target.value = target.value.substring(0, max);		
	}	
}

// JavaScript for collections and list
openxava.setFilterVisible = function(application, module, id, tabObject, visible) { 
    var filter = openxava.getElementById(application, module, "list_filter_" + id); 
    var showLink = openxava.getElementById(application, module, "show_filter_" + id);
    var hideLink = openxava.getElementById(application, module, "hide_filter_" + id);
    if (visible) {
    	$(filter).fadeIn();
    	$(showLink).hide(); 
    	$(hideLink).show(); 
    }
    else {
    	$(filter).fadeOut();
    	$(hideLink).hide(); 
    	$(showLink).show(); 
    }
	Tab.setFilterVisible(application, module, visible, tabObject);
}

openxava.customizeList = function(application, module, id) { 	
	var customizeControlsClass = openxava.decorateId(application, module, "customize_" + id); 
	$("." + customizeControlsClass).each(function() {
		if ($(this).is(":visible")) $(this).fadeOut();
		else $(this).fadeIn(2000);
	});
	var tableId = openxava.decorateId(application, module, id); 
	var firstRow = $("#" + tableId).children().children(":first"); 
	if (firstRow.hasClass(openxava.customizeControlsClass)) {			
		firstRow.removeClass(openxava.customizeControlsClass); 
	}
	else {
		firstRow.addClass(openxava.customizeControlsClass); 
	}
}

openxava.removeColumn = function(application, module, columnId, tabObject) {
	var th = $("#" + columnId).closest("th"); 
	var i = th.index() + 1;
	var table = th.closest("table");
	th.fadeOut();
    $(table).find("td:nth-child(" + i + ")").fadeOut();
	var property = $("#" + columnId).closest("th").attr("data-property");
	Tab.removeProperty(application, module, property, tabObject);
}

openxava.setPageRowCount = function(application, module, collection, select) {	
	openxava.executeAction(application, module, '', false, "List.setPageRowCount", "rowCount=" + select.value + ",collection=" + collection)
}

openxava.executeAction = function(application, module, confirmMessage, takesLong, action, argv, range, alreadyProcessed, inNewWindow) { 
	if (confirmMessage != "" && !confirm(confirmMessage)) return;
	if (takesLong) { 
		$('#xava_processing_layer').fadeIn(); 
	}
	var form = openxava.getForm(application, module);
	form[openxava.decorateId(application, module, "xava_focus_forward")].value = "false";
	form[openxava.decorateId(application, module, "xava_action")].value=action;	
	form[openxava.decorateId(application, module, "xava_action_argv")].value=argv;	
	form[openxava.decorateId(application, module, "xava_action_range")].value=range;
	form[openxava.decorateId(application, module, "xava_action_already_processed")].value=alreadyProcessed + "_";
	if (openxava.isSubmitNeeded(form)) { 
		if (!openxava.submitting) {  
			openxava.submitting = true;
			form.submit(); 
		} 
	} 
	else {
		openxava.ajaxRequest(application, module, false, inNewWindow); 
	}				
}

openxava.getFormValues = function(ele) { // A refinement of dwr.util.getFormValues
	if (ele != null) {
		if (ele.elements == null) {
			alert("getFormValues() requires an object or reference to a form element.");
			return null;
		}
		var reply = {};
		var name;
		var value;
		for (var i = 0; i < ele.elements.length; i++) {
			if (ele[i].type in {button:0,submit:0,reset:0,image:0,file:0}) continue;
			if (ele[i].name) {
				name = ele[i].name;
				value = openxava.getFormValue(ele[i]);
			}
			else continue; 
			if (reply[name] != null) continue; 
			if (value != null) { 
				reply[name] = value;
			}
		}		
		return reply;
	}
}

openxava.getFormValue = function(ele) { // A refinement of dwr.util.getValue
	
	if (dwr.util._isHTMLElement(ele, "select")) {
	    // Using "type" property instead of "multiple" as "type" is an official 
	    // client-side property since JS 1.1
	    if (ele.type == "select-multiple") {
	      return new Array();
	    }
	    else {
	      var sel = ele.selectedIndex;
	      if (sel != -1) {
	        var item = ele.options[sel];
	        var valueAttr = item.getAttributeNode("value");
	        if (valueAttr && valueAttr.specified) {
	          return item.value;
	        }
	        return item.text;
	      }
	      else {
	        return "";
	      }
	    }
	}

	if (dwr.util._isHTMLElement(ele, "input")) {
		if (ele.type == "checkbox" || ele.type == "radio") {
	      return ele.checked?ele.value:null; 
	    }
	    return ele.value;
	}

	return $(ele).val(); 
};


openxava.isSubmitNeeded = function(form) {		
	return form.enctype=="multipart/form-data";
}

openxava.onBlur = function(application, module, property) {
	openxava.getElementById(application, module, "xava_previous_focus").value = property;
	openxava.getElementById(application, module, "xava_current_focus").value = "";
}

openxava.onFocus = function(application, module, property) {
	openxava.getElementById(application, module, "xava_previous_focus").value = "";
	openxava.getElementById(application, module, "xava_current_focus").value = property;	
}

openxava.throwPropertyChanged = function(application, module, property) {
	if (openxava.isRequesting(application, module)) return;	
	document.throwPropertyChange = true;
	var form = openxava.getForm(application, module);
	form[openxava.decorateId(application, module, "xava_focus_forward")].value = "true";	
	form[openxava.decorateId(application, module, "xava_previous_focus")].value=property;
	form[openxava.decorateId(application, module, "xava_changed_property")].value=property;
	setTimeout ('openxava.requestOnChange("' + application + '", "' + module + '")', 100);	
}

openxava.calculate = function(application, module, propertyId, scale) {
	var calculation = $('#' + propertyId + "_CALCULATION_").val();
	var value = eval(calculation).toFixed(scale).replace(".", openxava.decimalSeparator);
	$('#' + propertyId).val(value);
	$('#' + propertyId).change(); 
}

openxava.getNumber = function(application, module, property) {
	var val = $('#' + openxava.decorateId(application, module, property)).val();
	if (val == '') return 0; 	
	return openxava.parseFloat(val); 
}

openxava.parseFloat = function(value) {
	value = value.replace(new RegExp("\\" + openxava.groupingSeparator, 'g'), "");
	value = value.replace(openxava.decimalSeparator, ".");
	return parseFloat(value); 
}

openxava.requestOnChange = function(application, module) {
	if (document.throwPropertyChange)  {
		openxava.ajaxRequest(application, module); 
	}			
}

openxava.setFocus = function(application, module) {		
	var form = openxava.getForm(application, module);	
	var elementName = form.elements[openxava.decorateId(application, module, "xava_focus_property_id")].value;
	var elementDecoratedName =  openxava.decorateId(application, module, elementName);
	if (!openxava.setFocusOnElement(form, elementDecoratedName)) {
		openxava.setFocusOnElement(form, elementDecoratedName + "__CONTROL__");
	}
}

openxava.setFocusOnElement = function(form, name) { 
	var element = form.elements[name];
	if (element != null && typeof element.disabled != "undefined" && !element.disabled) {		
		if (!$(element).is(':visible')) return false; 		
		if (element.type != "hidden") {			
			element.focus();
			return true; 
		}
		if (typeof element.select != "undefined") {			
			element.select();
			return true; 
		}
	}	
	return false;
}

openxava.clearCondition = function(application, module, prefix) { 
	openxava.clearConditionValues(application, module, prefix);
	openxava.clearConditionComparators(application, module, prefix);
	openxava.clearConditionValuesTo(application, module, prefix);
	var arg = null;
	if (prefix !== "") {
		var tokens = prefix.split("_");
		var collection = tokens[tokens.length - 2];
		arg = "collection=" + collection;
	}
	openxava.executeAction(application, module, '', false, 'List.filter', arg);
}

openxava.clearConditionValues = function(application, module, prefix) { 
	var form = openxava.getForm(application, module);
	var elementName = openxava.decorateId(application, module, prefix + "conditionValue.");
	var element = form.elements[elementName + "0"];
	var i=0;
	while (typeof element != "undefined") {
		element.value = '';
		element.placeholder = "";
		element = form.elements[elementName + (++i)];
	}
}

openxava.clearConditionComparators = function(application, module, prefix) {  
	var form = openxava.getForm(application, module);
	var elementName = openxava.decorateId(application, module, prefix + "conditionComparator.");
	var element = form.elements[elementName + "0"];
	var i=0;
	while (typeof element != "undefined") {
		if (element.type == "select-one") { 
			element[0].selected = 'selected';
		}
		element = form.elements[elementName + (++i)];
	}
}

openxava.clearConditionValuesTo = function(application, module, prefix) { 
	var form = openxava.getForm(application, module);
	var elementName = openxava.decorateId(application, module, prefix + "conditionValueTo.");
	var element = form.elements[elementName + "0"];
	var i=0;
	while (typeof element != "undefined") {
		element.value = '';
		element.style.display='none';
		element = form.elements[elementName + (++i)];
		$(element).next().hide();	// calendar image
	}
}

openxava.onSelectElement = function(application, module, action, argv, checkValue, idRow, hasOnSelectAction, selectedRowStyle, rowStyle, confirmMessage, takesLong, selectingAll, row, tabObject) {
	openxava.onChangeCheckBox(checkValue,row,application,module,tabObject);
	
	var id = $("#" + idRow)[0];
	if (checkValue) {
		$(id).addClass("_XAVA_SELECTED_ROW_").addClass(openxava.selectedRowClass);
		id.style.cssText = rowStyle + selectedRowStyle;
	}
	else {
		$(id).removeClass("_XAVA_SELECTED_ROW_").removeClass(openxava.selectedRowClass);
		id.style.cssText = rowStyle;
	}	
	if (hasOnSelectAction){
		argv = argv + ",selected=" + checkValue;
		openxava.executeAction(application, module, confirmMessage, takesLong, action, argv);	
	}	
	
	if (!selectingAll) openxava.hasOnSelectAll(application, module);
}

openxava.onSelectListFormat = function(event) { 
	var i = $(event.target);
	i.parent().parent().find("a").removeClass(openxava.selectedListFormatClass);
	i.parent().addClass(openxava.selectedListFormatClass);
}

openxava.clearLog = function() { 
	$('#xava_console').empty();
}

openxava.log = function(message) { 	
	$('#xava_console').append(message).append("<br/>");
}

openxava.onSelectAll = function(application, module, action, argv, checkValue, hasOnSelectAction, prefix, selectedRowStyle, rowStyle, tabObject){
	// search its deselected
	var name = openxava.decorateId(application, module, tabObject);
	var index = -1;
	var value = name + ":";
	for (var i=0; i<openxava.deselected.length; i++) {
		if (openxava.deselected[i].indexOf(name) == 0){
			index = i;
			value = openxava.deselected[i];
			break;
		}
	}
	if (index < 0) index = openxava.deselected.length;
	
	//
	var selected = document.getElementsByName(openxava.decorateId(application, module, "xava_selected"));
	var first = -1;
	var last = 0;
	var alreadyProcessed = "";
	var selectedPrefix = prefix == ""?"selected:":prefix;
	for (var i=0; i<selected.length; i++) {
		if (selected[i].value.indexOf(selectedPrefix) == 0){ 
			var row = selected[i].value.replace(prefix + "selected:", "");
			
			if (selected[i].checked && !checkValue){
				value += "[false" + row + "]";
			}
			else if (!selected[i].checked && checkValue){
				var c = "[false" + row + "]";
				if (value.indexOf(c) > 0){
					value = value.replace(c, "");
				}
			}
			
			if (selected[i].value.search("__SELECTED__") != -1) row = selected[i].value.replace(prefix + "__SELECTED__:", "");	// calculatedCollections
			if (first < 0) first = row;
			if (selected[i].checked==checkValue) {
				alreadyProcessed = alreadyProcessed + "_" + row;
			}
			else{
				var idRow = openxava.decorateId(application, module, prefix) + row;
				openxava.onSelectElement(application, module, action, argv, checkValue, idRow, null, selectedRowStyle, rowStyle, null, null, true, row, tabObject);  
				last = row;
			}
			selected[i].checked=checkValue?1:0;
		}
	}
	openxava.deselected[index] = value;
	
	if (first < 0) return;	// no items in the collection
	if (hasOnSelectAction){ 
		argv = argv + ",selected=" + checkValue;
		var range = first + "_" + last;
		openxava.executeAction(application, module, "", "", action, argv, range, alreadyProcessed);
	}
}

openxava.hasOnSelectAll = function(application, module){
	var selectedAll = document.getElementsByName(openxava.decorateId(application, module, "xava_selected_all"));
	for (var i=0; i<selectedAll.length; i++){
		var selected = document.getElementsByName(openxava.decorateId(application, module, "xava_selected"));
		var all = selected.length > 0;
		var e = 0;	// when there are several collections: it checks that the collection has at least one element
		
		var value = selectedAll[i].value.replace("selected_all", ""); 
		var selectedPrefix = value == ""?"selected:":value; 
		for (var j=0; j<selected.length && all; j++){
			if (selected[j].value.indexOf(selectedPrefix) == 0){ 
				e++;
				all = selected[j].checked;
			}
		}
		selectedAll[i].checked=e == 0 ? false : all;
	}
}

openxava.effectShow = function(application, module, id) {
	$("#"+openxava.decorateId(application, module, id)).hide(); 
	$("#"+openxava.decorateId(application, module, id)).fadeIn(); 
}

openxava.showFrame = function(id) { 
	$("#"+id+"content").slideDown(); 
	$("#"+id+"header").children().fadeOut(2000); 
	$("#"+id+"hide").show();
	$("#"+id+"show").hide();
	View.setFrameClosed(id, false);
}

openxava.hideFrame = function(id) {
	$("#"+id+"content").slideUp(); 
	$("#"+id+"header").children().fadeIn(2000); 
	$("#"+id+"hide").hide();
	$("#"+id+"show").show();
	View.setFrameClosed(id, true);
}

openxava.onChangeComparator = function(id,idConditionValue,idConditionValueTo,labelFrom,labelInValues) {
	var comparator = openxava.getFormValue(document.getElementById(id));
	var br = $('#' + id).prev();
	if (br.is('br')) br.remove();
	
	if ("range_comparator" == comparator){
		$('#' + idConditionValue).show().next().show();
		$('#' + idConditionValueTo).show().next().show();
		document.getElementById(idConditionValue).placeholder = labelFrom;
	}
	else if ("empty_comparator" == comparator || "not_empty_comparator" == comparator) {
		$('#' + id).before('<br/>');
		$('#' + idConditionValue).hide().next().hide();
		$('#' + idConditionValueTo).hide().next().hide();
	}
	else{
		$('#' + idConditionValue).show().next().show();
		$('#' + idConditionValueTo).hide().next().hide();		
		if ("in_comparator" == comparator || "not_in_comparator" == comparator) {
			document.getElementById(idConditionValue).placeholder = labelInValues;
		} 		
		else {
			document.getElementById(idConditionValue).placeholder = "";
		}
	}
}

openxava.onChangeCheckBox = function(cb,row,application,module,tabObject){
	var name = openxava.decorateId(application, module, tabObject);
	var index = -1;
	var value = name + ":";
	for (var i=0; i<openxava.deselected.length; i++) {
		if (openxava.deselected[i].indexOf(name) == 0){
			index = i;
			value = openxava.deselected[i];
			break;
		}
	}
	if (index < 0) index = openxava.deselected.length;
	if (cb == true){
		var c = "[" + !cb + row + "]";
		if (value.indexOf(c) > 0){
			value = value.replace(c, "");
		}
	}
	else {
		value = value + "[" + cb + row + "]";
	}
	openxava.deselected[index] = value;
}

openxava.subcontroller = function(id,containerId,buttonId,imageId,aId,spanId){
	// hidden the menu when click out
	$('html').click(function(e) {
		var elementId = e.target.id;
		if (aId != elementId &&
			buttonId != elementId && 
			imageId != elementId &&
			spanId != elementId){
			if ('none' != $('#'+id).css('display')){
				$('#'+id).css('display','none');
				$('#'+buttonId).removeClass(openxava.subcontrollerSelectedClass);
				$('#'+imageId).fadeTo("fast",1);
			}
		}
	});
	// hidden the menu when click the button a second time   
	if ('none' != $('#'+id).css('display')){
		$('#'+id).css('display','none');
		$('#'+buttonId).removeClass(openxava.subcontrollerSelectedClass);
		$('#'+imageId).fadeTo("fast",1);
		return;
	}
	// display and position the menu 
	$('#'+id).css('display','inline');
	$('#'+id).css({ 
		'top': $('#'+aId).position().top + $('#'+buttonId).outerHeight(true),
		'left': $('#'+aId).position().left
	});
	//
	$('#'+imageId).fadeTo("fast",0.3);
	$('#'+buttonId).addClass(openxava.subcontrollerSelectedClass);
}

openxava.watchColumnsSearch = function() {  
	jQuery( "#xava_search_columns_text" ).typeWatch({
		callback: openxava.filterColumns,
	    wait:500,
	    highlight:true,
	    captureLength:0
	});
	
	$( "#xava_search_columns_text" ).keyup(function() {
		if ($(this).val() == "") openxava.filterColumns(); 
	});
}

openxava.filterColumns = function() {
	Tab.filterColumns($("#xava_application").val(), $("#xava_module").val(), $("#xava_search_columns_text").val(), openxava.refreshColumnsList);
}

openxava.refreshColumnsList = function(columnsList) { 
	$('#xava_add_columns').html(columnsList);
}

openxava.markRowAsCut = function(collectionId, rowId) {
	$('#' + collectionId).find(".ox-cut-row").removeClass("ox-cut-row");
	if (rowId != null) $('#' + rowId).addClass('ox-cut-row'); 
	else $('#' + collectionId).find("._XAVA_SELECTED_ROW_").addClass('ox-cut-row');
}


openxava.fadeIn = function(selector, duration) { 
	$(selector).fadeIn(duration);
}

openxava.show = function(selector) { 
	$(selector).show();
}
