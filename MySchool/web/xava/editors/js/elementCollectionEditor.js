if (elementCollectionEditor == null) var elementCollectionEditor = {};

openxava.addEditorInitFunction(function() {
	$('.xava_sortable_elements').sortable({ 
		items: '>.xava_sortable_element_row',
		handle: ".xava_handle", 
	    stop: function( event, ui ) {
	    	var table = $(event.target).closest("table");	    	
	    	var row = table.find('tr').eq(1);
	    	elementCollectionEditor.renumber(row, 0);
	    }	
	});
});

elementCollectionEditor.onChangeRow = function(element, rowIndex) {
	var currentRow = $(element).parent().parent();
	var nextRow = currentRow.next();
	if (nextRow.is(':visible')) return;			
	var newRow = nextRow.clone();
	var token1 = new RegExp("__" + (rowIndex + 1), "g");
	var token2 = "__" + (rowIndex + 2);
	newRow.attr("id", newRow.attr("id").replace(token1, token2));
	var newRowHtml = newRow.html().replace(token1, token2);
	token1 = new RegExp(", " + (rowIndex + 1) + "\\)", "g");
	token2 = ", " + (rowIndex + 2) + ")";
	newRowHtml = newRowHtml.replace(token1, token2);
	token1 = new RegExp(", this, " + (rowIndex + 1) + ", ", "g");
	token2 = ", this, " + (rowIndex + 2) + ", ";
	newRowHtml = newRowHtml.replace(token1, token2);
	newRow.html(newRowHtml);
	newRow.css("display", "none"); 
	var table = currentRow.parent().parent();	
	elementCollectionEditor.setDefaultValues(table, rowIndex); 
	nextRow.show();
	$(nextRow).after(newRow);
	currentRow.children().first().find("nobr").css('visibility', 'visible');
	currentRow.addClass("xava_sortable_element_row"); 
	openxava.initEditors();
}

elementCollectionEditor.setDefaultValues = function(table, rowIndex) {
	var header = table.children().first().children().first(); 
	header.children("[id]").each(function() { 
		var headerId = $( this ).attr("id");
		var inputName = headerId.replace(new RegExp("__H", "g"), "__" + rowIndex);
		$("[name='" + inputName + "']").val($( this ).attr("data-default-value")); 		
	});
}

elementCollectionEditor.removeRow = function(application, module, element, rowIndex, hasTotals) { 
	var currentRow = $(element).parent().parent().parent().parent();
	var nextRow = currentRow.next();
	currentRow.remove();
	elementCollectionEditor.renumber(nextRow, rowIndex);
	if (hasTotals) {
		openxava.executeAction(application, module, "", false, "ElementCollection.refreshTotals");
	}	
}

elementCollectionEditor.renumber = function(row, rowIndex) { 
	var token1 = new RegExp("__\\d+", "g");
	var token2 = "__" + rowIndex;
	row.attr("id", row.attr("id").replace(token1, token2));
	row.find('input').each(function(){	    
	    $(this).attr('value',$(this).val());	    
	});
	var rowHtml = row.html()
		.replace(token1, token2)
		.replace(new RegExp("this, \\d+", "g"), "this, " + rowIndex)
		.replace(new RegExp("keyProperty=(.*)\\.\\d+\\.", "g"), "keyProperty=$1." + rowIndex + ".");
	row.html(rowHtml);	
	if ($(row).is(":visible")) { 
		elementCollectionEditor.renumber(row.next(), rowIndex + 1);
	}
}
