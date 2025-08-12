if (descriptionsEditor == null) var descriptionsEditor = {};

// Cargar script DWR de manera más robusta
var dwrScriptLoaded = false;
var dwrScriptAttempts = 0;
var MAX_DWR_ATTEMPTS = 3;

function loadDwrScript() {
    if (typeof Descriptions !== 'undefined') {
        dwrScriptLoaded = true;
        return;
    }
    
    if (dwrScriptAttempts >= MAX_DWR_ATTEMPTS) {
        console.warn("No se pudo cargar el script DWR después de " + MAX_DWR_ATTEMPTS + " intentos");
        return;
    }
    
    dwrScriptAttempts++;
    var script = document.createElement('script');
    script.src = openxava.contextPath + "/dwr/interface/Descriptions.js";
    script.onload = function() { 
        dwrScriptLoaded = true; 
        console.log("Script DWR cargado correctamente");
    };
    script.onerror = function() {
        console.error("Error al cargar el script DWR");
        setTimeout(loadDwrScript, 1000); // Reintentar en 1 segundo
    };
    document.head.appendChild(script);
}

loadDwrScript();

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
					
					// Verificar si DWR está disponible
					if (typeof Descriptions === 'undefined' || !Descriptions.getSuggestions) {
						console.warn("DWR Descriptions no disponible, usando lista vacía");
						response([]);
						return;
					}
					
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
								// Activar depuración para ver la respuesta
								window.DEBUG_DESCRIPTIONS = true;
								
								// Mostrar la respuesta exacta que llega
								console.log('DWR response raw:', items);
								console.log('DWR response type:', typeof items);
								if (typeof items === 'string') {
									console.log('DWR response string length:', items.length);
									console.log('DWR response string first 100 chars:', items.substring(0, 100));
								}
								
								if (window.DEBUG_DESCRIPTIONS) {
									console.log('Descriptions.getSuggestions response for', {
										term: request.term,
										model: model, keyProperty: keyProperty, keyProperties: keyProperties,
										descriptionProperty: descriptionProperty, descriptionProperties: descriptionProperties,
										condition: condition, orderByKey: orderByKey, order: order, limit: limit
									});
								}
							try {
								// Si es un string (posible JSON), intentar parsearlo
								if (typeof items === 'string') {
									try { 
										// Corregir formato de JSON si tiene comillas simples en lugar de dobles
										var fixedJson = items
											.replace(/([{,])\s*([a-zA-Z0-9_]+)\s*:/g, '$1"$2":')
											.replace(/:\s*'([^']*)'\s*([,}])/g, ':"$1"$2')
											.replace(/:\s*"([^"]*)"\s*([,}])/g, ':"$1"$2');
										console.log('Intentando parsear JSON corregido:', fixedJson.substring(0, 100) + '...');
										items = JSON.parse(fixedJson);
										console.log('JSON parseado correctamente');
									} catch(e) { 
										console.error('Error parseando JSON:', e);
										// Intento alternativo: parseo manual
										try {
											console.log('Intentando extraer datos manualmente');
											// Extraer pares label/value manualmente con regex
											var results = [];
											var regex = /\{\s*value\s*:\s*"([^"]+)"\s*,\s*label\s*:\s*"([^"]+)"\s*\}/g;
											var match;
											while ((match = regex.exec(items)) !== null) {
												results.push({
													value: match[1],
													label: match[2]
												});
											}
											console.log('Extracción manual exitosa, encontrados:', results.length);
											items = results;
										} catch(e2) {
											console.error('Extracción manual fallida:', e2);
											items = []; // Último recurso: lista vacía
										}
									}
								}
								
								// Si no es array, intentar convertirlo
								if (!$.isArray(items)) {
									try { 
										items = Array.prototype.slice.call(items); 
									}
									catch(e) { items = []; }
								}
								
								// Si los items no tienen la estructura correcta {label,value}, crear array vacío
								if (items.length > 0 && (typeof items[0] !== 'object' || !items[0].label)) {
									console.warn('Formato incorrecto de items en Descriptions.getSuggestions');
									items = [];
								}
								
								if (window.DEBUG_DESCRIPTIONS) console.log('items procesados:', items.length, items[0]);
								response(items);
							} catch(e) { 
								console.error('Error en procesamiento de respuesta DWR', e);
								response([]); 
							}
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
