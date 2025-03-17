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
		
		// Specific configuration for pie charts
		if (type === 'pie') {
			// Transform data format for pie charts
			var pieData = [];
			
			// For pie charts, we need to transform the data
			// We take the first dataset and combine it with labels
			if (data.length > 0) {
				var firstDataSet = data[0];
				
				// Skip the first element which is the column name
				for (var i = 1; i < firstDataSet.length; i++) {
					if (i-1 < labels.length) {
						pieData.push([labels[i-1], firstDataSet[i]]);
					}
				}
			}
			
			// Replace the original data with the pie-formatted data
			chartConfig.data.columns = pieData;
			
			// Configure pie labels to show values instead of percentages
			chartConfig.pie = {
				label: {
					format: function(value, ratio, id) {
						return value; // Show value instead of percentage
					}
				}
			};			
		}
		
		c3.generate(chartConfig);	
	});

});
