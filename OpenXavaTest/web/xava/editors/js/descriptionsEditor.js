if (descriptionsEditor == null) var descriptionsEditor = {};

openxava.addEditorInitFunction(function() {
	
	$(".xava_select").each(function() { 
		$(this).autocomplete({
			source: eval($(this).data("values")), 
			minLength: 0,
			disabled: true, // tmp
			select: function( event, ui ) {
				$(event.target).val(ui.item.label);
				$(event.target).next().val(ui.item.value);
				$(event.target).next().next().val(ui.item.label);
				event.preventDefault();
				descriptionsEditor.executeOnChange($(event.target));
			},
			focus: function( event, ui ) {
				$(event.target).val(ui.item.label);
				event.preventDefault();
			},			
			change: function( event, ui ) {
				if ($(event.target).val() === "" && $(event.target).next().val() !== "") {  
					$(event.target).next().val("");
					$(event.target).next().next().val("");
					descriptionsEditor.executeOnChange($(event.target));
				}
				else if ($(event.target).val() !== $(event.target).next().next().val()){
					$(event.target).val("");
					$(event.target).next().val("");
					$(event.target).next().next().val("");
				}
			},
			search: function( event, ui ) {
				$(event.target).next().next().next().hide();
				$(event.target).next().next().next().next().show();
			},
			close: function( event, ui ) {
				$(event.target).next().next().next().next().hide();
				$(event.target).next().next().next().show();	
				if ($(event.target).val() !== $(event.target).next().next().val()) {
					// To work clicking outside combo after mouse hover in plain view and dialog
					if ($(event.target).val() === "") $(event.target).val("");
					else $(event.target).val($(event.target).next().next().val()); 
				}				
			},
			appendTo: "body"
		}); 	
		
		$(this).attr("autocomplete", "nope");
		
		// tmp ini
		// TMP ME QUEDÉ POR AQUÍ. INTENTANDO QUE EL IE11 NO SAQUE LOS COMBOS, CON Product2
		$(this).click({
			//$(this).autocomplete("disabled", "false");
			alert("Hola");
		});
		// tmp fin
	});

	// tmp ini
	//$(this).autocomplete( "close" ); // tmp
	setTimeout("descriptionsEditor.close('ox_OpenXavaTest_Product2__subfamily___number')", 500);
	console.log("[descriptionsEditor.js] "); // tmp
	// tmp fin
	
});

descriptionsEditor.open = function(id) {
	var control = $("#" + id).prev();
	control.autocomplete( "search", "" );
	control.focus(); 
}

descriptionsEditor.close = function(id) {
	var element = $("#" + id);
	element.prev().autocomplete( "close" );
}

descriptionsEditor.executeOnChange = function(element) {
	$(element).parent().trigger("change"); 
	var onchange = element.attr("onchange");
	if (typeof onchange == 'undefined') return;
	eval(onchange);
}