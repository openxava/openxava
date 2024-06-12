/* tmr Mover a openxava*/

openxava.addEditorInitFunction(function() {
	// tmr Ha de funcionar con más de uno
	$('.xava_collection_chart').each( function () {
		var labels = $(this).data("labels");
		console.log("labels=" + labels); // tmr
		console.log("labels[0]=" + labels[0]); // tmr		
		var values = $(this).data("values");
		console.log("values=" + values); // tmr
		console.log("values[0]=" + values[0]); // tmr		
		var parentId = $(this).parent().attr("id");
		c3.generate({
		    bindto: '#' + parentId + " .xava_collection_chart", 
	 		data: {
		        columns: [[ values ], ["B", 100, 200]], // TMR ME QUEDÉ POR AQUÍ, INTENTANDO QUE FUNCIONEN 2 BARRAS
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
