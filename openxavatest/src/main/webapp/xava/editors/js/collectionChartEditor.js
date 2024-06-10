/* tmr Mover a openxava*/

openxava.addEditorInitFunction(function() {
	// tmr Ha de funcionar con más de uno
	// tmr Debería usar jquery en vez de nativo
	$('.xava_collection_chart').each( function () {
		// TMR ME QUEDÉ POR AQUÍ, NO FUNCIONA EL PARSE
		var labels = JSON.parse($(this).data("labels"));
		console.log("labels=" + labels); // tmr
		var values = JSON.parse($(this).data("values"));
		console.log("values=" + values); // tmr
		var parentId = $(this).parent().attr("id");
		console.log("parentId=" + parentId); // tmr		
	
		c3.generate({
		    bindto: '#' + parentId + " .xava_collection_chart", 
	 		data: {
		        columns: [ values ],
		        type: 'bar'
		    },
		    axis: {
		        x: {
		            type: 'category',
		            categories: labels
		        }
		    },	    
		    bar: {
		        width: {
		            ratio: 0.5 // tmr ¿Quitar? ¿Ajustar?
		        }
		    }
		});	
	});

});
