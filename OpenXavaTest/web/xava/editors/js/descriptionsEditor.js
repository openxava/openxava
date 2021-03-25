if (descriptionsEditor == null) var descriptionsEditor = {};

openxava.addEditorInitFunction(function() {
	
	$(".xava_select").each(function() { 
		$(this).autocomplete({
			source: eval($(this).data("values")), 
			minLength: 0,
			disabled: true, // For IE11 not open combos on init with accents
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
			// tmp ini
			source: function( request, response ) {
				/*
				var matcher = new RegExp( $.ui.autocomplete.escapeRegex( request.term ), "i" );
				response( $.grep( names, function( value ) {
					value = value.label || value.value || value;
					return matcher.test( value ) || matcher.test( normalize( value ) );
				}) );
				*/
				var input = $(this)[0]["element"];
				
				console.log("[descriptionsEditor.source] input.name=" + $(input).attr('name')); // tmp
				
				var svalues = $(input).data("values");
				console.log("[descriptionsEditor.source] svalues>" + svalues + "<"); // tmp
				console.log("[descriptionsEditor.source] typeof svalues=" + typeof svalues); // tmp
				
				// TMP ME QUEDÉ POR AQUÍ: FALLA EL PARSE, CREO PORQUE LE FALTAN LAS COMILLAS ALREDEDOR DE LAS CLAVES. 
				var values = JSON.parse(svalues); 
				console.log("[descriptionsEditor.source] values=" + values); // tmp
				console.log("[descriptionsEditor.source] typeof values=" + typeof values); // tmp
				
				
				/*
				var obj = $(this)[0]["element"];
				for (var i in obj) {
				    if (obj.hasOwnProperty(i)) {
				    	console.log("[descriptionsEditor.source] obj["+ i + "]=" +obj[i]); // tmp	
				    }
				}
				*/
				
				var names = [ "Jörn Zaefferer", "Scott González", "John Resig" ];
				response(values);
			},
			// tmp fin
			appendTo: "body"
		}); 	
		
		$(this).attr("autocomplete", "nope");

		var editor = $(this);
		$(this).parent().click(function() {
			editor.autocomplete("enable");
		});
		$(this).focus(function() {
			editor.autocomplete("enable");
		});		

	});

	
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