/* tmr */

openxava.addEditorInitFunction(function() {
	// tmr Ha de funcionar con más de uno
	// tmr Debería usar jquery en vez de nativo
	// Accede a los datos almacenados en el atributo 'data-datos'
	var element = document.getElementById('employeesChart');
	if (element == null) return; 
	var datos = element.getAttribute('data-datos');
	console.log("datos=" + datos); // tmr
	var datosAlmacenados = JSON.parse(datos);
	
	// Extrae los nombres y los valores de datos para C3
	var nombres = datosAlmacenados.map(function(d) { return d.nombre; });
	var data1 = ['data1'].concat(datosAlmacenados.map(function(d) { return d.data1; }));
	// tmr var data2 = ['data2'].concat(datosAlmacenados.map(function(d) { return d.data2; }));

	var chart = c3.generate({
	    bindto: '#employeesChart', // Este es el id del elemento HTML donde se renderizará el gráfico
 		data: {
	        columns: [
	            data1 //,
	            // data2
	        ],
	        type: 'bar'
	    },
	    axis: {
	        x: {
	            type: 'category',
	            categories: nombres
	        }
	    },	    
	    bar: {
	        width: {
	            ratio: 0.5 // Esto hace que el ancho de la barra sea la mitad del ancho disponible.
	        }
	    }
	});
});
