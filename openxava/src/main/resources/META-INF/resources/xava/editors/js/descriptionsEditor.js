if (descriptionsEditor == null) var descriptionsEditor = {};

// Cargar script DWR usando la utilidad estándar de OpenXava (como en discussionEditor.js)
openxava.getScript(openxava.contextPath + "/dwr/interface/Descriptions.js");

openxava.addEditorInitFunction(function() {
	
	$(".xava_select").each(function() { 
		$(this).autocomplete({
			minLength: 0,
			// Configurar scroll infinito
			open: function() {
				var input = $(this);
				var menu = input.autocomplete("widget");
				
				// Eliminar handler anterior si existe
				menu.off("scroll.infiniteScroll");
				
				// Añadir handler de scroll
				menu.on("scroll.infiniteScroll", function() {
					// Comprobar si estamos cerca del final
					var scrollHeight = menu.prop("scrollHeight");
					var scrollTop = menu.scrollTop();
					var menuHeight = menu.height();
					var triggerPoint = scrollHeight - menuHeight - 50; // 50px antes del final
					
					// Si estamos cerca del final y hay más elementos, cargar más
					if (scrollTop >= triggerPoint && input.data("hasMoreItems") && !input.data("loadingMore")) {
						console.log("Scroll infinito: cargando más elementos...");
						
						// Marcar como cargando para evitar múltiples cargas
						input.data("loadingMore", true);
						
						// Guardar el valor actual del input
						var currentValue = input.val();
						
						// Forzar una nueva búsqueda con el mismo término
						setTimeout(function() {
							// Restaurar el valor original para evitar filtrado por elemento seleccionado
							input.val(input.data("lastTerm") || "");
							
							// Mantener el menú abierto
							var wasOpen = input.autocomplete("widget").is(":visible");
							
							// Realizar la búsqueda
							input.autocomplete("search", input.data("lastTerm") || "");
							
							// Asegurar que el menú permanece abierto
							if (wasOpen) {
								input.autocomplete("widget").show();
							}
							
							// Restaurar la posición de scroll
							var menu = input.autocomplete("widget");
							var scrollHeight = menu.prop("scrollHeight");
							var menuHeight = menu.height();
							menu.scrollTop(scrollHeight - menuHeight - 60); // Un poco más arriba del punto de activación
						}, 50);
					}
				});
			},
			select: function(event, ui) {
				console.log("[descriptionsEditor.js::select] "); // tmr
				/* tmr Creado por Claude, no funciona bien
				var input = $(this);
				
				// Comportamiento normal para elementos regulares
				var hidden = input.next();
				hidden.val(ui.item.value);
				input.val(ui.item.label);
				input.data("changed", "true");
				input.change();
				return false;
				*/
				
				// tmr ini Original
				$(event.target).val(ui.item.label);
				$(event.target).next().val(ui.item.value);
				$(event.target).next().next().val(ui.item.label);
				event.preventDefault();
				descriptionsEditor.executeOnChange($(event.target));
				// tmr fin
			},
			focus: function( event, ui ) {
				$(event.target).val(ui.item.label);
				event.preventDefault();
			},			
			change: function( event, ui ) {
				console.log("[descriptionsEditor.js::change] "); // tmr
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
                console.log("[descriptionsEditor.js::close] "); // tmr
                $(event.target).next().next().next().next().hide();
                $(event.target).next().next().next().show();
                console.log("[descriptionsEditor.js::close] $(event.target).val()=" + $(event.target).val()); // tmr
                console.log("[descriptionsEditor.js::close] $(event.target).next().next().val()=" + $(event.target).next().next().val()); // tmr
                if ($(event.target).val() !== $(event.target).next().next().val()) {
                    // To work clicking outside combo after mouse hover in plain view and dialog
                    if ($(event.target).val() === "") $(event.target).val("");
                    else $(event.target).val($(event.target).next().next().val()); 
                }

                // Reset pagination state so reopening does not append more items
                var $input = $(event.target);
                $input.data("allItems", []);
                $input.data("hasMoreItems", true);
                $input.data("loadingMore", false);
                // Keep lastTerm as-is to preserve UX; alternatively uncomment next line to force full reset
                // $input.data("lastTerm", null);
            },
			source: function( request, response ) {
				
				var input = $(this)[0]["element"];
				var isRemote = $(input).data("remote") === true || $(input).data("remote") === "true";
				if (isRemote) { // tmr ¿Siempre remoto?
					var propertyKey = $(input).next().attr("id");
					var viewObject = $(input).data("view-object") || "xava_view";
					var limit = 30; // Máximo de elementos a mostrar por página
					var offset = 0; // Offset inicial para paginación
					var condition = $(input).data("condition") || "";
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

					// tmr Simplificar el código. No tan defensivo
					
					// Reiniciar offset si es una nueva búsqueda
					if (request.term !== $(input).data("lastTerm")) {
						offset = 0;
						$(input).data("lastTerm", request.term);
						$(input).data("allItems", []);
					} else {
						// Si es continuación de búsqueda anterior, usar offset
						offset = $(input).data("allItems") ? $(input).data("allItems").length : 0;
					}
					
					// Verificar si DWR está disponible
					if (typeof Descriptions === 'undefined' || !Descriptions.getSuggestions) {
						console.warn("DWR Descriptions no disponible, usando lista vacía");
						response([]);
						return;
					}
					
					Descriptions.getSuggestions( // tmr ¿Son necesarios todos los parámetros?
						openxava.lastApplication, openxava.lastModule,
						propertyKey, viewObject,
						request.term, limit,
						condition, orderByKey, order,
						filter, descriptionsFormatter,
						parameterValuesProperties, parameterValuesStereotypes,
						model, keyProperty, keyProperties,
						descriptionProperty, descriptionProperties,
						offset, // Nuevo parámetro para paginación
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
								
								// Verificar que items sea un array
								if (!$.isArray(items)) {
									console.error("Formato incorrecto de items en Descriptions.getSuggestions");
									items = [];
								}
								
								// Acumular items para paginación
								var allItems = $(input).data("allItems") || [];
								
								// Añadir nuevos items a la lista acumulada
								if (items.length > 0) {
									allItems = allItems.concat(items);
									$(input).data("allItems", allItems);
								}
								
								// Guardar si hay más elementos disponibles
								if (items.length >= limit) {
									$(input).data("hasMoreItems", true);
								} else {
									$(input).data("hasMoreItems", false);
								}
								
								// Restablecer flag de carga
								$(input).data("loadingMore", false);
								
								console.log("items procesados:", items.length, "total acumulado:", allItems.length);
								response(allItems);
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

/* tmr original
descriptionsEditor.removeAccents = function(str) { 
	return str.toLowerCase()
		.replace(/[áàâä]/,"a")
		.replace(/[éèêë]/,"e")
		.replace(/[íìîï]/,"i")
		.replace(/[óòôö]/,"o")
		.replace(/[úùûü]/,"u");	
}
*/

/* tmr Propuesta por ChatGPT */
descriptionsEditor.removeAccents = function(str) {
    return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
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
