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
		
		var input = $(this).parent().parent().find("input[type='text']");
		coordinatesEditor.setView(map, input);		
		 	
		input.change(function() {
			coordinatesEditor.setView(map, input);
		});
	});
});

coordinatesEditor.setView = function(map, input) {
	var coordinates = input.val().split(/,/);
	// TMP ME QUEDÉ POR AQUÍ: FALLA CUANDO EL CAMPO ESTÁ VACÍO
	console.log("[coordinatesEditor.setView] coordinates=" + coordinates)
	map.setView(coordinates, 16);
}