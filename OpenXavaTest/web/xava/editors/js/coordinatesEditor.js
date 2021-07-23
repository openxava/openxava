if (coordinatesEditor == null) var coordinatesEditor = {};

openxava.addEditorInitFunction(function() {
	$(".xava_coordinates").each(function() {
		var map = L.map(this);
		console.log("[openxava.addEditorInitFunction] openxava.mapsTileSize=" + openxava.mapsTileSize); // tmp
		console.log("[openxava.addEditorInitFunction] openxava.mapsZoomOffset=" + openxava.mapsZoomOffset); // tmp
		L.tileLayer(openxava.mapsTileProvider, {
			maxZoom: 18,
			attribution: openxava.mapsAttribution,
			tileSize: openxava.mapsTileSize,
			zoomOffset: openxava.mapsZoomOffset
		}).addTo(map);
		
		var markers = L.layerGroup().addTo(map);
				
		var input = $(this).parent().find("input[type='text']");
		input.parent().removeClass("ox-error-editor"); // tmp Coger de Style
		coordinatesEditor.setView(map, input, markers);		
		 	
		input.change(function() {
			if (coordinatesEditor.isValid(input)) {
				input.parent().removeClass("ox-error-editor"); // tmp Coger de Style
				markers.clearLayers();
				coordinatesEditor.setView(map, input, markers);
			}
			else {
				input.parent().addClass("ox-error-editor"); // tmp Coger de Style
			}
		});
		
		map.on('click', function(e) {
			markers.clearLayers();
			L.marker(e.latlng).addTo(markers);
			input.val(e.latlng.lat + ", " + e.latlng.lng);
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