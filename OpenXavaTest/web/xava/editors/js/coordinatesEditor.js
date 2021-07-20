openxava.addEditorInitFunction(function() {
	var mymap = L.map('mapid').setView([39.45399631909972, -0.3838714157032522], 16);
	// TMP PONER LA ATRIBUTION BIEN
	// TMP ME QUEDÉ POR AQUÍ: FALLA DICIENDO "Map container not found"
	/*
	L.tileLayer('https://b.tile.opentopomap.org/{z}/{x}/{y}.png', {
		maxZoom: 18,
		attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, ' +
			'Imagery &copy; <a href="https://opentopomap.org">OpenTopoMap</a> (<a href="https://creativecommons.org/licenses/by-sa/3.0/">CC-BY-SA</a>)',
		id: 'mapbox/streets-v11'
	}).addTo(mymap);
	*/
});