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
	console.log("[elementCollectionEditor.onChangeRow] 10 " ); // tmr
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
	console.log("[elementCollectionEditor.onChangeRow] 999" ); // tmr
}

elementCollectionEditor.setDefaultValues = function(table, rowIndex) {
	console.log("[elementCollectionEditor.setDefaultValues] 10"); // tmr
	var header = table.children().first().children().first(); 
	header.children("[id]").each(function() { 
		var headerId = $( this ).attr("id");
		console.log("[elementCollectionEditor.setDefaultValues] headerId=" + headerId); // tmr
		var inputName = headerId.replace(new RegExp("__H", "g"), "__" + rowIndex);
		console.log("[elementCollectionEditor.setDefaultValues] inputName=" + inputName); // tmr
		console.log("[elementCollectionEditor.setDefaultValues] val()=" + $("[name='" + inputName + "']").val()); // tmr
		if ($("[name='" + inputName + "']").val() === "" ) { 
			console.log("[elementCollectionEditor.setDefaultValues] default-value=" + $( this ).attr("data-default-value")); // tmr 
			$("[name='" + inputName + "']").val($( this ).attr("data-default-value"));
			// tmr ini
	
		
			 var control = $("#ox_OpenXavaTest_ProductExpenses2__expenses___1___product___number").prev();	
			 control.val("4");
			 
			 
			 // console.log("[elementCollectionEditor.setDefaultValues] control.data('values')=" + control.data('values')); // tmr
			 
			 // TMR ME QUEDÉ POR AQUÍ. BUSCAR EN EL ARRAY PODRÍA SER UNA OPCIÓN, AUNQUE SEA CON UN BUCLE
			 var stringValues = control.attr('data-values');
			 
			 console.log("[elementCollectionEditor.setDefaultValues] stringValues=" + stringValues); // tmr
			 
			 var jsonValues = JSON.parse(stringValues);
			 console.log("[elementCollectionEditor.setDefaultValues] jsonValues=" + jsonValues); // tmr
			 
			 var cuatro = jsonValues['4'];
			 console.log("[elementCollectionEditor.setDefaultValues] cuatro=" + cuatro); // tmr
			 console.log(cuatro); // tmr 
			 
			 
			 
			 // $("#ox_OpenXavaTest_ProductExpenses2__expenses___1___product___number").prev().autocomplete( 'search' );
			
			//$("#ox_OpenXavaTest_ProductExpenses2__expenses___1___product___number").prev().data('ui-autocomplete')._trigger('select', 'autocompleteselect', {item:{value:'2'}});
			
			/*
			//$("[name='" + inputName + "__CONTROL__ " + "']").val($( this ).attr("data-default-value"));
			//ox_OpenXavaTest_ProductExpenses2__expenses___0___invoice__KEY__
			// 
    		//$("#ox_OpenXavaTest_ProductExpenses2__expenses___1___invoice__KEY__").prev().val("2011 1");
    		//$("#ox_OpenXavaTest_ProductExpenses2__expenses___1___invoice__KEY__").prev().val('{"label":"2011 1","value":"[.1.2011.]"}');
    		//$("#ox_OpenXavaTest_ProductExpenses2__expenses___1___invoice__KEY__").val("[.1.2011.]");
    		//$("#ox_OpenXavaTest_ProductExpenses2__expenses___1___invoice__KEY__").trigger('change');
    		//$("#ox_OpenXavaTest_ProductExpenses2__expenses___0___invoice__KEY____CONTROL__").val("[.1.2011.]");
    		//$("#ox_OpenXavaTest_ProductExpenses2__expenses___0___invoice__KEY____CONTROL__").trigger('change');
    		*/
			// tmr fin			
		} 		
	});
	console.log("[elementCollectionEditor.setDefaultValues] 999"); // tmr
}

elementCollectionEditor.removeRow = function(application, module, element, rowIndex, hasTotals) { 
	var currentRow = $(element).parent().parent().parent().parent();
	var nextRow = currentRow.next();
	currentRow.remove();
	elementCollectionEditor.renumber(nextRow, rowIndex);
	if (hasTotals) {
		openxava.executeAction(application, module, "", false, "ElementCollection.refreshTotals");
	}	
	openxava.initEditors();
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
	if ($(row).css("display") !== 'none') { // is:visible/hidden not work on mobile (removing one record removes all until end)
		elementCollectionEditor.renumber(row.next(), rowIndex + 1);
	}
}
