if (coordinatesEditor == null) var coordinatesEditor = {};

openxava.addEditorInitFunction(function() {
	$(".xava_coordinates").each(function() {
		var map = L.map(this);
		L.tileLayer(openxava.mapsTileProvider, {
			maxZoom: 18,
			attribution: openxava.mapsAttribution,
			tileSize: openxava.mapsTileSize,
			zoomOffset: openxava.mapsZoomOffset
		}).addTo(map);
		
		var markers = L.layerGroup().addTo(map);
				
		var input = $(this).parent().find("input[type='text']");
		input.parent().removeClass(openxava.errorEditorClass); 
		coordinatesEditor.setView(map, input, markers);		
		 	
		input.change(function() {
			if (coordinatesEditor.isValid(input)) {
				input.parent().removeClass(openxava.errorEditorClass); 
				markers.clearLayers();
				coordinatesEditor.setView(map, input, markers);
			}
			else {
				input.parent().addClass(openxava.errorEditorClass);
			}
		});
		
		input.on("paste", function(e) {
			setTimeout(function(){ input.blur(); }, 100);
		});
		
		map.on('click', function(e) {
			if (!input.is(':disabled')) { 
				markers.clearLayers();
				L.marker(e.latlng).addTo(markers);
				input.val(e.latlng.lat + ", " + e.latlng.lng);
				var onchange = input.attr('onchange');
				if (onchange != null) eval(onchange);					
			} 
		});
		
		$(this).data("map", map); 
		
	});
});

openxava.addEditorDestroyFunction(function() {
	$(".xava_coordinates").each(function() {
		var map = $(this).data("map");
		map.off();
		map.remove();
		$(this).data("map", null);
	});
});


coordinatesEditor.setView = function(map, input, markers) {
	var coordinates = input.val().split(/,/);
	if (coordinates.length == 2) {
		map.setView(coordinates, 15);
		L.marker(coordinates).addTo(markers);
	}
	else {
		map.setView([0, 0], 1);  
		if(navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(function(position) {
				map.setView([position.coords.latitude, position.coords.longitude], 7);
			});
		}	
	}	
}

coordinatesEditor.isValid = function(input) {
	var re = /^[-+]?([1-8]?\d(\.\d+)?|90(\.0+)?),\s*[-+]?(180(\.0+)?|((1[0-7]\d)|([1-9]?\d))(\.\d+)?)$/;
	var coordinates = input.val().trim(); 
	if (coordinates === "") return true;
	return re.exec(coordinates);
}