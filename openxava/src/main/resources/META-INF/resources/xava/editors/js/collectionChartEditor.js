openxava.addEditorInitFunction(function() {
	$('.xava_collection_chart').each( function () {
		var labels = $(this).data("labels");
		var data = $(this).data("data");
		var parentId = $(this).parent().attr("id");
		var type = $(this).data("type");
		c3.generate({
		    bindto: '#' + parentId + " .xava_collection_chart", 
	 		data: {
		        columns: data, 
		        type: type
		    },
		    axis: {
		        x: {
		            type: 'category',
		            categories: labels
		        }
		    },	    
		    bar: {
		        width: {
		            ratio: 0.5 
		        }
		    }
		});	
	});

});
