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
			source: function( request, response ) {
				var input = $(this)[0]["element"];
				var values = $(input).data("values");
				var matcher = new RegExp($.ui.autocomplete.escapeRegex(descriptionsEditor.removeAccents(request.term)), "i");
				response( $.grep( values, function( value ) {
					return matcher.test(descriptionsEditor.removeAccents(value.label));
				}) );
			},
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

descriptionsEditor.removeAccents = function(str) { 
	return str.toLowerCase()
		.replace(/[באגה]/,"a")
		.replace(/[יטךכ]/,"e")
		.replace(/[םלמן]/,"i")
		.replace(/[ףעפצ]/,"o")
		.replace(/[תשûü]/,"u");	
}

descriptionsEditor.is = function(input) {
	return input.prev().hasClass('ui-autocomplete-input');
}

descriptionsEditor.val = function(input, defaultValue) {
	input.val(defaultValue);
	var control = input.prev();	
	var values = control.data('values');
	var label = descriptionsEditor._getLabel(values, defaultValue);
	control.val(label);
	input.next().val(label);
}  

descriptionsEditor._getLabel = function(values, value) {
	for (i in values) { // Arrays.find not supported by HtmlUnit
	    if (values[i].value == value) {
	        return values[i].label;
	    }
	}			
	return "";
}
