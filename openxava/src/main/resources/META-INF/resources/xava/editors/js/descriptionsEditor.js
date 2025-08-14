if (descriptionsEditor == null) var descriptionsEditor = {};

// Load DWR script using OpenXava helper (same approach as discussionEditor.js)
openxava.getScript(openxava.contextPath + "/dwr/interface/Descriptions.js");

openxava.addEditorInitFunction(function() {
	
	$(".xava_select").each(function() { 
		$(this).autocomplete({
			minLength: 0,
			// Configure infinite scroll
			open: function() {
				var input = $(this);
				var menu = input.autocomplete("widget");
				
				// Remove previous scroll handler if any
				menu.off("scroll.infiniteScroll");
				
				// Add scroll handler
				menu.on("scroll.infiniteScroll", function() {
					// Check if we are near the bottom
					var scrollHeight = menu.prop("scrollHeight");
					var scrollTop = menu.scrollTop();
					var menuHeight = menu.height();
					var triggerPoint = scrollHeight - menuHeight - 50; // 50px before the end
					
					// If near the end and more items are available, load more
					if (scrollTop >= triggerPoint && input.data("hasMoreItems") && !input.data("loadingMore")) {
						console.log("Infinite scroll: loading more items...");
						
						// Mark as loading to avoid duplicate loads
						input.data("loadingMore", true);
						
						// Keep current input value
						var currentValue = input.val();
						
						// Trigger a new search with the same term
						setTimeout(function() {
							// Restore original value to avoid filtering by selected item
							input.val(input.data("lastTerm") || "");
							
							// Keep the menu open
							var wasOpen = input.autocomplete("widget").is(":visible");
							
							// Perform the search
							input.autocomplete("search", input.data("lastTerm") || "");
							
							// Ensure the menu remains open
							if (wasOpen) {
								input.autocomplete("widget").show();
							}
							
							// Restore scroll position
							var menu = input.autocomplete("widget");
							var scrollHeight = menu.prop("scrollHeight");
							var menuHeight = menu.height();
							menu.scrollTop(scrollHeight - menuHeight - 60); // Slightly above trigger point
						}, 50);
					}
				});
			},
			select: function(event, ui) {
				console.log("[descriptionsEditor.js::select]");			
				// Original behavior
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
				console.log("[descriptionsEditor.js::change]");
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
				console.log("[descriptionsEditor.js::close]");
				$(event.target).next().next().next().next().hide();
				$(event.target).next().next().next().show();
				console.log("[descriptionsEditor.js::close] value=" + $(event.target).val());
				console.log("[descriptionsEditor.js::close] hidden description=" + $(event.target).next().next().val());
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
				if (isRemote) { // Possibly always remote in large datasets
					var propertyKey = $(input).next().attr("id");
					var viewObject = $(input).data("view-object") || "xava_view";
					var limit = 30; // Max items per page
					var offset = 0; // Initial offset for pagination
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

					// TODO: Simplify code, reduce defensive branches
					
					// Reset offset when term changes (new search)
					if (request.term !== $(input).data("lastTerm")) {
						offset = 0;
						$(input).data("lastTerm", request.term);
						$(input).data("allItems", []);
					} else {
						// Continue from previous accumulated items
						offset = $(input).data("allItems") ? $(input).data("allItems").length : 0;
					}
					
					// Check DWR availability
					if (typeof Descriptions === 'undefined' || !Descriptions.getSuggestions) {
						console.warn("DWR Descriptions not available, using empty list");
						response([]);
						return;
					}
					
					Descriptions.getSuggestions( // TODO: Are all these parameters necessary?
						openxava.lastApplication, openxava.lastModule,
						propertyKey, viewObject,
						request.term, limit,
						condition, orderByKey, order,
						filter, descriptionsFormatter,
						parameterValuesProperties, parameterValuesStereotypes,
						model, keyProperty, keyProperties,
						descriptionProperty, descriptionProperties,
						offset, // New pagination parameter
							function(items) { 
								// Enable debugging to inspect responses
								window.DEBUG_DESCRIPTIONS = true;
								
								// Log raw response data
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
								// If it is a string (possible JSON), try to parse it
								if (typeof items === 'string') {
									try { 
										// Fix JSON format if single quotes are used instead of double quotes
										var fixedJson = items
											.replace(/([{,])\s*([a-zA-Z0-9_]+)\s*:/g, '$1"$2":')
											.replace(/:\s*'([^']*)'\s*([,}])/g, ':"$1"$2')
											.replace(/:\s*"([^"]*)"\s*([,}])/g, ':"$1"$2');
										console.log('Trying to parse fixed JSON:', fixedJson.substring(0, 100) + '...');
										items = JSON.parse(fixedJson);
										console.log('JSON parsed successfully');
									} catch(e) { 
										console.error('Error parsing JSON:', e);
										// Fallback: manual extraction
										try {
											console.log('Trying manual data extraction');
											// Extract label/value pairs manually with regex
											var results = [];
											var regex = /\{\s*value\s*:\s*"([^"]+)"\s*,\s*label\s*:\s*"([^"]+)"\s*\}/g;
											var match;
											while ((match = regex.exec(items)) !== null) {
												results.push({
													value: match[1],
													label: match[2]
												});
											}
											console.log('Manual extraction succeeded, found:', results.length);
											items = results;
										} catch(e2) {
											console.error('Manual extraction failed:', e2);
											items = []; // Last resort: empty list
										}
									}
								}
								
								// If not an array, try to convert
								if (!$.isArray(items)) {
									try { 
										items = Array.prototype.slice.call(items); 
									}
									catch(e) { items = []; }
								}
								
								// Ensure items is an array
								if (!$.isArray(items)) {
									console.error("Incorrect items format from Descriptions.getSuggestions");
									items = [];
								}
								
								// Accumulate items for pagination
								var allItems = $(input).data("allItems") || [];
								
								// Append new items to the accumulated list
								if (items.length > 0) {
									allItems = allItems.concat(items);
									$(input).data("allItems", allItems);
								}
								
								// Set whether more items are available
								if (items.length >= limit) {
									$(input).data("hasMoreItems", true);
								} else {
									$(input).data("hasMoreItems", false);
								}
								
								// Reset loading flag
								$(input).data("loadingMore", false);
								
								console.log("processed items:", items.length, "accumulated total:", allItems.length);
								response(allItems);
							} catch(e) { 
								console.error('Error processing DWR response', e);
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

/* Original implementation kept for reference
descriptionsEditor.removeAccents = function(str) { 
    return str.toLowerCase()
        .replace(/[áàâä]/,"a")
        .replace(/[éèêë]/,"e")
        .replace(/[íìîï]/,"i")
        .replace(/[óòôö]/,"o")
        .replace(/[úùûü]/,"u"); 
}
*/

/* Alternative implementation */
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
