if (coordinatesEditor == null) var coordinatesEditor = {};

openxava.addEditorInitFunction(function() {
	$(".xava_coordinates").each(function() {
		// var map = L.map(this).setView([39.45399631909972, -0.3838714157032522], 16);
		var map = L.map(this); // tmp .setView([39.45399631909972, -0.3838714157032522], 16);
		L.tileLayer('https://b.tile.opentopomap.org/{z}/{x}/{y}.png', {
			maxZoom: 18,
			attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, ' +
				'Imagery &copy; <a href="https://opentopomap.org">OpenTopoMap</a> (<a href="https://creativecommons.org/licenses/by-sa/3.0/">CC-BY-SA</a>)'
		}).addTo(map);
		
		var markers = L.layerGroup().addTo(map);
				
		var input = $(this).parent().parent().find("input[type='text']");
		input.parent().removeClass("ox-error-editor"); // tmp Coger de Style
		coordinatesEditor.setView(map, input, markers);		
		 	
		input.change(function() {
			if (coordinatesEditor.isValid(input)) {
				input.parent().removeClass("ox-error-editor"); // tmp Coger de Style
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