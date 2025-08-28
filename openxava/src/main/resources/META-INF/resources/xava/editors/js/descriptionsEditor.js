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
						// removed debug log
						
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
                // Always use on-demand loading for better performance
                var propertyKey = $(input).next().attr("id");
                    var limit = 60; // Max items per page
                    var offset = 0; // Initial offset for pagination

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
						if (typeof Descriptions === 'undefined' || !Descriptions.getDescriptions) {
							response([]);
							return;
						}
					
					Descriptions.getDescriptions(
						openxava.lastApplication, openxava.lastModule,
						propertyKey,
						request.term, limit,
						offset,
						function(items) { 
							// Normalize items into an array of {value,label,...}
							var list = [];
							if (typeof items === 'string') {
								// Server returns a JSON-like string with unquoted keys; fix and parse
								var fixedJson = items
									.replace(/([{,])\s*([a-zA-Z0-9_]+)\s*:/g, '$1"$2":')
									.replace(/:\s*'([^']*)'\s*([,}])/g, ':"$1"$2')
									.replace(/:\s*"([^"]*)"\s*([,}])/g, ':"$1"$2');
									try {
										list = JSON.parse(fixedJson);
									} catch (e) {
										list = [];
									}
							} else if ($.isArray(items)) {
								list = items;
							} else {
								list = [];
							}

							// Post-process labels (unicode handling)
							if ($.isArray(list)) {
								list = list.map(function(it){
									if (it && typeof it.label === 'string') {
										var copy = $.extend({}, it);
										copy.label = descriptionsEditor._convertUPlusToBackslashU(copy.label);
										copy.label = descriptionsEditor._decodeUnicodeEscapes(copy.label);
										if (copy.label && typeof copy.label.normalize === 'function') copy.label = copy.label.normalize('NFC');
										return copy;
									}
									return it;
								});
							}

							// Accumulate items for pagination
							var allItems = $(input).data("allItems") || [];
							if (list.length > 0) {
								allItems = allItems.concat(list);
								$(input).data("allItems", allItems);
							}

							// Set whether more items are available
							$(input).data("hasMoreItems", list.length >= limit);

							// Reset loading flag and respond
							$(input).data("loadingMore", false);
							response(allItems);
						}
					);
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
    return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
}


// Convert occurrences like "U+00C9" into a literal "\u00C9" (do not decode to a character)
descriptionsEditor._convertUPlusToBackslashU = function(s) {
    if (typeof s !== 'string') return s;
    return s.replace(/U\+([0-9A-Fa-f]{4,6})/g, function(_, hex){ return "\\u" + hex.toUpperCase(); });
}

// Decode sequences like "\u00C9" or "\u{1F600}" into actual Unicode characters
descriptionsEditor._decodeUnicodeEscapes = function(s) {
    if (typeof s !== 'string') return s;
    // Replace literal backslash-u sequences with actual characters
    // \u{XXXXX} form (ES6)
    var out = s.replace(/\\u\{([0-9A-Fa-f]+)\}/g, function(_, hex){
        try { return String.fromCodePoint(parseInt(hex, 16)); } catch(e2) { return _; }
    });
    // \uXXXX form
    out = out.replace(/\\u([0-9A-Fa-f]{4})/g, function(_, hex){
        try { return String.fromCharCode(parseInt(hex, 16)); } catch(e3) { return _; }
    });
    // Also handle double-escaped sequences "\\\\uXXXX" and "\\\\u{...}" by first collapsing to single backslash then decoding again
    out = out.replace(/\\\\u\{([0-9A-Fa-f]+)\}/g, function(_, hex){
        try { return String.fromCodePoint(parseInt(hex, 16)); } catch(e4) { return _; }
    });
    out = out.replace(/\\\\u([0-9A-Fa-f]{4})/g, function(_, hex){
        try { return String.fromCharCode(parseInt(hex, 16)); } catch(e5) { return _; }
    });
    return out;
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
