if (descriptionsEditor == null) var descriptionsEditor = {};

openxava.getScript(openxava.contextPath + "/dwr/interface/Descriptions.js"); 

openxava.addEditorInitFunction(function() {
	
	$(".xava_select").each(function() { 
		$(this).autocomplete({
			minLength: 0,
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
				var isRemote = $(input).data("remote") === true || $(input).data("remote") === "true";
				if (isRemote) {
					var propertyKey = $(input).next().attr("id");
					var viewObject = $(input).data("view-object") || "xava_view";
					var limit = $(input).data("limit") || 30;
					var condition = $(input).data("condition") || $(input).data("condicion") || "";
					var orderByKey = $(input).data("orderbykey") || $(input).data("ordenadoporclave") || "";
					var order = $(input).data("order") || $(input).data("orden") || "";
					var filter = $(input).data("filter") || $(input).data("filtro") || "";
					var descriptionsFormatter = $(input).data("descriptionsformatter") || $(input).data("formateadordescripciones") || "";
					var parameterValuesProperties = $(input).data("parametervaluesproperties") || $(input).data("propiedadesvaloresparametros") || "";
					var parameterValuesStereotypes = $(input).data("parametervaluesstereotypes") || $(input).data("estereotiposvaloresparametros") || "";
					var model = $(input).data("model") || "";
					var keyProperty = $(input).data("keyproperty") || "";
					var keyProperties = $(input).data("keyproperties") || "";
					var descriptionProperty = $(input).data("descriptionproperty") || "";
					var descriptionProperties = $(input).data("descriptionproperties") || "";
					Descriptions.getSuggestions(
						openxava.lastApplication, openxava.lastModule,
						propertyKey, viewObject,
						request.term, limit,
						condition, orderByKey, order,
						filter, descriptionsFormatter,
						parameterValuesProperties, parameterValuesStereotypes,
						model, keyProperty, keyProperties,
						descriptionProperty, descriptionProperties,
						function(items) { 
							if (window.DEBUG_DESCRIPTIONS) {
								console.log('Descriptions.getSuggestions response for', {
									term: request.term,
									model: model, keyProperty: keyProperty, keyProperties: keyProperties,
									descriptionProperty: descriptionProperty, descriptionProperties: descriptionProperties,
									condition: condition, orderByKey: orderByKey, order: order, limit: limit
								});
							}
							try {
								if (!$.isArray(items)) {
									try { items = Array.prototype.slice.call(items); }
									catch(e) { items = []; }
								}
								if (window.DEBUG_DESCRIPTIONS) console.log('items length', items.length, items[0]);
								response(items);
							} catch(e) { response([]); }
						}
					);
				}
				else {
					var values = $(input).data("values") || [];
					var matcher = new RegExp($.ui.autocomplete.escapeRegex(descriptionsEditor.removeAccents(request.term)), "i");
					response( $.grep( values, function( value ) {
						return matcher.test(descriptionsEditor.removeAccents(value.label));
					}) );
				}
			},
			appendTo: "body"
		}); 	
		
		$(this).attr("autocomplete", "nope");
		
		$('.xava_descriptions_editor_open').off('click').click(function() {
			descriptionsEditor.open($(this).data('property-key'));
		});
		$('.xava_descriptions_editor_close').off('click').click(function() {
			descriptionsEditor.close($(this).data('property-key'));
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
}

descriptionsEditor.removeAccents = function(str) { 
	return str.toLowerCase()
		.replace(/[áàâä]/,"a")
		.replace(/[éèêë]/,"e")
		.replace(/[íìîï]/,"i")
		.replace(/[óòôö]/,"o")
		.replace(/[úùûü]/,"u");	
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
