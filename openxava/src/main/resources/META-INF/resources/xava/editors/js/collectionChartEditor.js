openxava.addEditorInitFunction(function() {
	$('.xava_collection_chart').each( function () {
		var labels = $(this).data("labels");
		var data = $(this).data("data");
		var parentId = $(this).parent().attr("id");
		var type = $(this).data("type");
		
		var chartConfig = {
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
		};
		
		if (type === 'pie') {
			var pieData = [];
			
			if (data.length > 0) {
				var firstDataSet = data[0];
				
				for (var i = 1; i < firstDataSet.length; i++) {
					if (i-1 < labels.length) {
						pieData.push([labels[i-1], firstDataSet[i]]);
					}
				}
			}
			
			chartConfig.data.columns = pieData;
			
			chartConfig.pie = {
				label: {
					format: function(value, ratio, id) {
						return value; 
					}
				}
			};			
		}
		
		c3.generate(chartConfig);	
	});

});
